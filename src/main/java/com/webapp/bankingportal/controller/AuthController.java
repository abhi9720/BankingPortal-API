package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for the OTP-based password-reset flow.
 *
 * <p>All endpoints in this controller are publicly accessible (no JWT required).
 * The three-step flow is: send OTP → verify OTP and receive reset token →
 * submit new password with the reset token.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Generates an OTP and sends it to the email address associated with the given identifier.
     *
     * @param otpRequest request containing the user identifier (email or account number)
     * @return {@code 200 OK} on successful OTP dispatch, or {@code 500} on email failure
     */
    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestBody OtpRequest otpRequest) {
        return authService.sendOtpForPasswordReset(otpRequest);
    }

    /**
     * Verifies the OTP and, on success, issues a single-use password-reset token.
     *
     * @param otpVerificationRequest request containing the identifier and the submitted OTP
     * @return {@code 200 OK} with the reset token, or {@code 401} if the OTP is invalid
     */
    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<String> verifyOtpAndIssueResetToken(
            @RequestBody OtpVerificationRequest otpVerificationRequest) {
        return authService.verifyOtpAndIssueResetToken(otpVerificationRequest);
    }

    /**
     * Resets the user's password using the one-time reset token obtained from the previous step.
     *
     * @param resetPasswordRequest request containing the identifier, reset token, and new password
     * @return {@code 200 OK} on success, {@code 401} for an invalid reset token, or {@code 500} on failure
     */
    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return authService.resetPassword(resetPasswordRequest);
    }

}
