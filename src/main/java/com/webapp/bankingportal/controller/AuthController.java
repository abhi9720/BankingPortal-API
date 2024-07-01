package com.webapp.bankingportal.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequestv2;
import com.webapp.bankingportal.dto.OtpVerificationRequestv2;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.service.AuthService;
import com.webapp.bankingportal.service.OtpService;
import com.webapp.bankingportal.service.UserService;
import com.webapp.bankingportal.util.ValidationUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final OtpService otpService;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(OtpService otpService, UserService userService, AuthService authService) {
        this.otpService = otpService;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequestv2 otpRequest) {
        final String identifier = otpRequest.getIdentifier();
        logger.info("Received OTP request for identifier: {}", identifier);

        User user;
        if (ValidationUtil.isValidEmail(identifier)) {
            user = userService.getUserByEmail(identifier).get();
        } else if (ValidationUtil.isValidAccountNumber(identifier)) {
            user = userService.getUserByAccountNumber(identifier).get();
        } else {
            logger.warn("Invalid identifier provided: {}", identifier);
            return ResponseEntity.badRequest().body("Invalid identifier provided");
        }

        if (user == null) {
            logger.warn("User not found for identifier: {}", identifier);
            return ResponseEntity.badRequest().body("User not found for the given identifier");
        }

        // Generate and send OTP
        final String accountNumber = user.getAccount().getAccountNumber();
        logger.info("Generating OTP for account number: {}", accountNumber);
        final String generatedOtp = otpService.generateOTP(accountNumber);
        final CompletableFuture<Void> emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(),
                user.getName(),
                accountNumber,
                generatedOtp);

        final ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format("{\"message\": \"OTP sent successfully to: %s\"}", user.getEmail()));

        final ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format("{\"message\": \"Failed to send OTP to: %s\"}", user.getEmail()));

        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequestv2 otpVerificationRequest) {
        String identifier = otpVerificationRequest.getIdentifier();
        String otp = otpVerificationRequest.getOtp();
        logger.info("Received OTP verification request for identifier: {}", identifier);

        if (identifier == null || identifier.isEmpty()) {
            logger.warn("Missing identifier in OTP verification request");
            return ResponseEntity.badRequest().body("Missing account number");
        }

        if (otp == null || otp.isEmpty()) {
            logger.warn("Missing OTP in OTP verification request");
            return ResponseEntity.badRequest().body("Missing OTP");
        }

        User user;
        if (ValidationUtil.isValidEmail(identifier)) {
            user = userService.getUserByEmail(identifier).get();
        } else if (ValidationUtil.isValidAccountNumber(identifier)) {
            user = userService.getUserByAccountNumber(identifier).get();
        } else {
            logger.warn("Invalid identifier provided: {}", identifier);
            return ResponseEntity.badRequest().body("Invalid identifier provided");
        }

        if (user == null) {
            logger.warn("User not found for identifier: {}", identifier);
            return ResponseEntity.badRequest().body("User not found for the given identifier");
        }
        String accountNumber = user.getAccount().getAccountNumber();
        logger.info("Validating OTP for account number: {}", accountNumber);

        // Validate OTP
        boolean isValidOtp = otpService.validateOTP(accountNumber, otp);
        if (!isValidOtp) {
            logger.warn("Invalid OTP provided for account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        String resetToken;
        try {
            resetToken = authService.generatePasswordResetToken(user);
        } catch (IllegalArgumentException e) {
            logger.error("Error generating password reset token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        String jsonResponse = "{\"passwordResetToken\": \"" + resetToken + "\"}";
        logger.info("Password reset token issued successfully for user: {}", user.getId());

        return ResponseEntity.ok(jsonResponse.toString());
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String identifier = resetPasswordRequest.identifier();
        String resetToken = resetPasswordRequest.resetToken();
        String newPassword = resetPasswordRequest.newPassword();
        logger.info("Received password reset request for identifier: {}", identifier);

        User user;
        if (ValidationUtil.isValidEmail(identifier)) {
            user = userService.getUserByEmail(identifier).get();
        } else if (ValidationUtil.isValidAccountNumber(identifier)) {
            user = userService.getUserByAccountNumber(identifier).get();
        } else {
            logger.warn("Invalid identifier provided: {}", identifier);
            return ResponseEntity.badRequest().body("Invalid identifier provided");
        }

        boolean isValidResetToken = authService.verifyPasswordResetToken(resetToken, user);
        if (!isValidResetToken) {
            logger.warn("Invalid reset token provided for user: {}", user.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid reset token");
        }

        // Reset password for the user
        boolean passwordResetSuccessful;
        try {
            passwordResetSuccessful = userService.resetPassword(user, newPassword);
        } catch (Exception e) {
            logger.error("Error resetting password for user: {}", user.getId(), e);
            return ResponseEntity.internalServerError().body("Failed to reset password");
        }

        if (passwordResetSuccessful) {
            logger.info("Password reset successfully for user: {}", user.getId());
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Password reset successfully\"}");
        } else {
            logger.error("Failed to reset password for user: {}", user.getId());
            return ResponseEntity.internalServerError().body("Failed to reset password");
        }
    }
}
