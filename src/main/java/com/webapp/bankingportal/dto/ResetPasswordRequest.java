package com.webapp.bankingportal.dto;

/**
 * Request body for completing a password reset.
 *
 * @param identifier  either the user's email address or 6-digit account number
 * @param resetToken  the single-use UUID token issued after successful OTP verification
 * @param newPassword the desired new password (must meet the password complexity requirements)
 */
public record ResetPasswordRequest(String identifier, String resetToken, String newPassword) {
}
