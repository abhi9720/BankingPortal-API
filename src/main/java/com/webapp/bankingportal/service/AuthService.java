package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;

import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.ResetPasswordRequest;
import com.webapp.bankingportal.entity.User;

/**
 * Service contract for the password-reset authentication flow.
 *
 * <p>The typical flow is:
 * <ol>
 *   <li>User requests an OTP via {@link #sendOtpForPasswordReset}.</li>
 *   <li>User submits the OTP; a one-time reset token is issued via
 *       {@link #verifyOtpAndIssueResetToken}.</li>
 *   <li>User submits the reset token and their new password via
 *       {@link #resetPassword}.</li>
 * </ol>
 * </p>
 */
public interface AuthService {

    /**
     * Generates (or reuses an existing valid) password-reset token for the given user.
     *
     * <p>If an unexpired token exists for the user it is returned directly;
     * otherwise a new UUID token is created and persisted.</p>
     *
     * @param user the user requesting a password reset
     * @return the opaque reset token string
     */
    String generatePasswordResetToken(User user);

    /**
     * Verifies that the provided reset token is valid for the given user and
     * has not expired, then deletes the token to prevent reuse.
     *
     * @param token the reset token to verify
     * @param user  the user who is expected to own the token
     * @return {@code true} if the token is valid and belongs to {@code user}, {@code false} otherwise
     */
    boolean verifyPasswordResetToken(String token, User user);

    /**
     * Permanently deletes the password-reset token identified by the given string.
     *
     * @param token the reset token to delete
     */
    void deletePasswordResetToken(String token);

    /**
     * Generates a new OTP and sends it to the email address associated with
     * the identifier in the request.
     *
     * @param otpRequest request containing the user identifier (email or account number)
     * @return {@code 200 OK} with a success message, or {@code 500} on email delivery failure
     */
    ResponseEntity<String> sendOtpForPasswordReset(OtpRequest otpRequest);

    /**
     * Validates the OTP submitted by the user and, on success, issues a
     * single-use password-reset token.
     *
     * @param otpVerificationRequest request containing the identifier and OTP
     * @return {@code 200 OK} with the reset token, or {@code 401} if the OTP is invalid
     */
    ResponseEntity<String> verifyOtpAndIssueResetToken(OtpVerificationRequest otpVerificationRequest);

    /**
     * Resets the user's password after verifying the one-time reset token.
     *
     * @param resetPasswordRequest request containing the identifier, reset token, and new password
     * @return {@code 200 OK} on success, {@code 401} for invalid token, or {@code 500} on failure
     */
    ResponseEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest);

}
