package com.webapp.bankingportal.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidOTPException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.security.JwtTokenUtil;
import com.webapp.bankingportal.service.OTPService;
import com.webapp.bankingportal.service.UserService;
import com.webapp.bankingportal.util.LoggedinUser;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final OTPService otpService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(
            UserController.class);

    public UserController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            UserDetailsService userDetailsService,
            OTPService otpService) {

        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            UserResponse userResponse = new UserResponse(registeredUser);

            return ResponseEntity.ok(objectMapper.writeValueAsString(userResponse));

        } catch (UserInvalidException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (JsonProcessingException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user with the account number and password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getAccountNumber(),
                    loginRequest.getPassword()));

        } catch (BadCredentialsException e) {

            // Invalid credentials, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

        // If authentication successful, generate JWT token
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getAccountNumber());
        logger.info("User logged in successfully: {}",
                loginRequest.getAccountNumber());
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        String accountNumber = otpRequest.getAccountNumber();
        if (!userService.doesAccountExist(accountNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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

        String response = "Failed to send OTP to: " + user.getEmail();
        try {
            if (emailSendingFuture.get()) {
                response = "OTP sent successfully to: " + user.getEmail();
            }
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            logger.error("Failed to send OTP to: {}", user.getEmail(), e);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        String accountNumber = otpVerificationRequest.getAccountNumber();
        String otp = otpVerificationRequest.getOtp();

        if (accountNumber == null || accountNumber.isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing account number");
        }

        if (otp == null || otp.isEmpty()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing OTP");
        }

        // Validate OTP against the stored OTP in the database
        try {
            boolean isValidOtp = otpService.validateOTP(accountNumber, otp);

            if (!isValidOtp) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("OTP has expired");
            }
        } catch (InvalidOTPException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

        // If OTP is valid, generate and return a JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountNumber);
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
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

            return ResponseEntity.ok(objectMapper.writeValueAsString(userResponse));

        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");

        } catch (UserInvalidException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (JsonProcessingException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user");
        }
    }
}
