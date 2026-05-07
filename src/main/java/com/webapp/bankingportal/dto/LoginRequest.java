package com.webapp.bankingportal.dto;

/**
 * Request body for standard password-based login.
 *
 * @param identifier either the user's email address or 6-digit account number
 * @param password   the plain-text password to authenticate with
 */
public record LoginRequest(String identifier, String password) {
}
