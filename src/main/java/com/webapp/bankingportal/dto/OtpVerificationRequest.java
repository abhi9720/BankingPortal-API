package com.webapp.bankingportal.dto;

/**
 * Request body for verifying an OTP submitted by a user.
 *
 * @param identifier either the user's email address or 6-digit account number
 * @param otp        the 6-digit OTP value previously sent to the user's email
 */
public record OtpVerificationRequest(String identifier, String otp) {
}
