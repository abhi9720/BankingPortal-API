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

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.security.JwtTokenUtil;
import com.webapp.bankingportal.service.OTPService;
import com.webapp.bankingportal.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final OTPService otpService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, AuthenticationManager authenticationManager,
            JwtTokenUtil jwtTokenUtil,
            UserDetailsService userDetailsService, OTPService otpService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setName(registeredUser.getName());
        userResponse.setEmail(registeredUser.getEmail());
        userResponse.setAccountNumber(registeredUser.getAccount().getAccountNumber());
        userResponse.setIFSC_code(registeredUser.getAccount().getIFSC_code());
        userResponse.setBranch(registeredUser.getAccount().getBranch());
        userResponse.setAccount_type(registeredUser.getAccount().getAccount_type());

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user with the account number and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getAccountNumber(),
                            loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            // Invalid credentials, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid account number or password");
        }

        // If authentication successful, generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getAccountNumber());
        logger.info("User logged in successfully: {}", loginRequest.getAccountNumber());
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpRequest otpRequest) {
        String accountNumber = otpRequest.getAccountNumber();
        if (!userService.doesAccountExist(accountNumber)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found for the given account number");
        }

        User user = userService.getUserByAccountNumber(accountNumber).get();
        String generatedOtp = otpService.generateOTP(accountNumber);
        CompletableFuture<Boolean> emailSendingFuture = otpService.sendOTPByEmail(user.getEmail(), user.getName(),
                accountNumber, generatedOtp);

        String response = "{\"message\": \"Failed to send OTP\"}";
        try {
            if (emailSendingFuture.get()) {
                response = "{\"message\": \"OTP sent successfully\"}";
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send OTP to user: {}", user.getEmail(), e);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        String accountNumber = otpVerificationRequest.getAccountNumber();
        String otp = otpVerificationRequest.getOtp();

        if (accountNumber == null || otp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Missing account number or OTP\"}");
        }

        // Validate OTP against the stored OTP in the database
        boolean isValidOtp = otpService.validateOTP(accountNumber, otp);

        if (!isValidOtp) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid OTP\"}");
        }

        // If OTP is valid, generate and return a JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountNumber);
        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok("{ \"token\": \"" + token + "\" }");
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody User user) {
        User updateUser = userService.updateUser(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setName(updateUser.getName());
        userResponse.setEmail(updateUser.getEmail());
        userResponse.setAccountNumber(updateUser.getAccount().getAccountNumber());
        userResponse.setIFSC_code(updateUser.getAccount().getIFSC_code());
        userResponse.setBranch(updateUser.getAccount().getBranch());
        userResponse.setAccount_type(updateUser.getAccount().getAccount_type());

        return ResponseEntity.ok(userResponse);
    }
}
