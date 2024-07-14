package com.webapp.bankingportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.service.AuthService;
import com.webapp.bankingportal.service.OtpService;
import com.webapp.bankingportal.service.UserService;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(OtpService otpService, UserService userService, AuthService authService) {
        this.otpService = otpService;
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
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

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
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

        // Validate OTP
        val isValidOtp = otpService.validateOTP(accountNumber, otp);
        if (!isValidOtp) {
            log.warn("Invalid OTP provided for account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        String resetToken;
        try {
            resetToken = authService.generatePasswordResetToken(user);
        } catch (IllegalArgumentException e) {
            log.error("Error generating password reset token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        log.info("Password reset token issued successfully for account: {}", accountNumber);

        return ResponseEntity.ok("{\"passwordResetToken\": \"" + resetToken + "\"}");
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        val identifier = resetPasswordRequest.identifier();
        val resetToken = resetPasswordRequest.resetToken();
        val newPassword = resetPasswordRequest.newPassword();
        log.info("Received password reset request for identifier: {}", identifier);

        val user = userService.getUserByIdentifier(identifier)
                .orElseThrow(() -> new UnauthorizedException("User not found for the given identifier"));

        val isValidResetToken = authService.verifyPasswordResetToken(resetToken, user);
        if (!isValidResetToken) {
            log.warn("Invalid reset token provided for user: {}", user.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid reset token");
        }

        // Reset password for the user
        boolean passwordResetSuccessful;
        try {
            passwordResetSuccessful = userService.resetPassword(user, newPassword);
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", user.getId(), e);
            return ResponseEntity.internalServerError().body("Failed to reset password");
        }

        if (passwordResetSuccessful) {
            log.info("Password reset successfully for user: {}", user.getId());
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Password reset successfully\"}");
        } else {
            log.error("Failed to reset password for user: {}", user.getId());
            return ResponseEntity.internalServerError().body("Failed to reset password");
        }
    }

}
