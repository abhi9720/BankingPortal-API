package com.webapp.bankingportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.service.TokenService;
import com.webapp.bankingportal.service.OtpService;
import com.webapp.bankingportal.service.UserService;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;

import jakarta.servlet.http.HttpServletRequest;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final OtpService otpService;

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
        val registeredUser = userService.registerUser(user);
        val userResponse = new UserResponse(registeredUser);

        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {

        val identifier = loginRequest.identifier();
        log.info("Received login request for identifier: {}", identifier);

        val user = userService.getUserByIdentifier(identifier)
                .orElseThrow(() -> new UnauthorizedException("User not found for the given identifier"));

        val accountNumber = user.getAccount().getAccountNumber();

        log.info("Authenticating Account: {}", accountNumber);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                accountNumber,
                loginRequest.password()));

        log.info("Account: {} authenticated successfully", accountNumber);

        userService.sendLoginNotificationEmail(
                userService.getUserByAccountNumber(accountNumber).get(),
                request.getRemoteAddr());

        val userDetails = userDetailsService
                .loadUserByUsername(accountNumber);

        val token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);

        log.info("Account: {} logged in successfully", accountNumber);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        val identifier = otpRequest.identifier();
        log.info("Received OTP request for identifier: {}", identifier);

        val user = userService.getUserByIdentifier(identifier)
                .orElseThrow(() -> new UnauthorizedException("User not found for the given identifier"));

        // Generate and send OTP
        val accountNumber = user.getAccount().getAccountNumber();
        log.info("Generating OTP for account number: {}", accountNumber);
        val generatedOtp = otpService.generateOTP(accountNumber);
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(),
                user.getName(),
                accountNumber,
                generatedOtp);

        val successResponse = ResponseEntity
                .ok(String.format("{\"message\": \"OTP sent successfully to: %s\"}", user.getEmail()));

        val failureResponse = ResponseEntity.internalServerError()
                .body(String.format("{\"message\": \"Failed to send OTP to: %s\"}", user.getEmail()));

        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(
            @RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {

        val identifier = otpVerificationRequest.identifier();
        val otp = otpVerificationRequest.otp();
        log.info("Received OTP verification request for identifier: {}", identifier);

        if (identifier == null || identifier.isEmpty()) {
            log.warn("Missing identifier in OTP verification request");
            return ResponseEntity.badRequest().body("Missing account number");
        }

        if (otp == null || otp.isEmpty()) {
            log.warn("Missing OTP in OTP verification request");
            return ResponseEntity.badRequest().body("Missing OTP");
        }

        val user = userService.getUserByIdentifier(identifier)
                .orElseThrow(() -> new UnauthorizedException("User not found for the given identifier"));

        val accountNumber = user.getAccount().getAccountNumber();
        log.info("Validating OTP for account number: {}", accountNumber);

        // Validate OTP against the stored OTP in the database
        val isValidOtp = otpService.validateOTP(accountNumber, otp);
        if (!isValidOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("OTP has expired");
        }

        // If OTP is valid, generate and return a token
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        val accountNumber = LoggedinUser.getAccountNumber();

        log.info("Authenticating account: {} ...", accountNumber);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                accountNumber,
                user.getPassword()));
        log.info("Account: {} authenticated successfully", accountNumber);

        log.info("Updating account: {} ...", accountNumber);
        val updatedUser = userService.updateUser(user);

        log.info("Account: {} is updated successfully", accountNumber);

        val userResponse = new UserResponse(updatedUser);

        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}",
                tokenService.getUsernameFromToken(token));

        return new ModelAndView("redirect:/logout");
    }

}
