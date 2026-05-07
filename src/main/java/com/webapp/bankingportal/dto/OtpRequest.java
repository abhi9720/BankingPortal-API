package com.webapp.bankingportal.dto;

/**
 * Request body for generating and sending an OTP to a user.
 *
 * @param identifier either the user's email address or 6-digit account number
 *                   used to look up the target account
 */
public record OtpRequest(String identifier) {
}
