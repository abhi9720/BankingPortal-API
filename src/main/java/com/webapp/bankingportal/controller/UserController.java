package com.webapp.bankingportal.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.service.TokenService;
import com.webapp.bankingportal.service.OtpService;
import com.webapp.bankingportal.service.UserService;
import com.webapp.bankingportal.util.LoggedinUser;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final OtpService otpService;

    private static final Logger logger = LoggerFactory.getLogger(
            UserController.class);

    public UserController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserDetailsService userDetailsService,
            OtpService otpService) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        UserResponse userResponse = new UserResponse(registeredUser);

        return ResponseEntity.ok(userResponse.toString());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {

        final String accountNumber = loginRequest.getAccountNumber();

        logger.info("Authenticating Account: {}", accountNumber);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                accountNumber,
                loginRequest.getPassword()));

        logger.info("Account: {} authenticated successfully", accountNumber);

        userService.sendLoginNotificationEmail(
                userService.getUserByAccountNumber(accountNumber).get(),
                request.getRemoteAddr());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(accountNumber);

        final String token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);

        logger.info("Account: {} logged in successfully", accountNumber);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        String accountNumber = otpRequest.getAccountNumber();
        if (!userService.doesAccountExist(accountNumber)) {
            return ResponseEntity.badRequest()
                    .body("User not found for the given account number");
        }

        User user = userService.getUserByAccountNumber(accountNumber).get();

        // Generate and send OTP
        String generatedOtp = otpService.generateOTP(accountNumber);
        CompletableFuture<Boolean> emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(),
                user.getName(),
                accountNumber,
                generatedOtp);

        final ResponseEntity<String> response = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send OTP to: " + user.getEmail());

        return emailSendingFuture.thenApply(success -> {
            if (success) {
                String jsonResponse = String.format("{\"message\": \"OTP sent successfully to: %s\"}", user.getEmail());
                return ResponseEntity.ok(jsonResponse);
            } else {
                return response;
            }
        }).exceptionally(e -> response).join();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(
            @RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {

        String accountNumber = otpVerificationRequest.getAccountNumber();
        String otp = otpVerificationRequest.getOtp();

        if (accountNumber == null || accountNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing account number");
        }

        if (otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing OTP");
        }

        // Validate OTP against the stored OTP in the database
        boolean isValidOtp = otpService.validateOTP(accountNumber, otp);
        if (!isValidOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("OTP has expired");
        }

        // If OTP is valid, generate and return a token
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountNumber);
        String token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        String accountNumber = LoggedinUser.getAccountNumber();

        logger.info("Authenticating account: {} ...", accountNumber);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                accountNumber,
                user.getPassword()));
        logger.info("Account: {} authenticated successfully", accountNumber);

        logger.info("Updating account: {} ...", accountNumber);
        User updatedUser = userService.updateUser(user);
        logger.info("Account: {} is updated successfully", accountNumber);

        UserResponse userResponse = new UserResponse(updatedUser);

        return ResponseEntity.ok(userResponse.toString());
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        logger.info("User logged out successfully {}",
                tokenService.getUsernameFromToken(token));

        return new ModelAndView("redirect:/logout");
    }
}
