package com.webapp.bankingportal.dto;

/**
 * Standard error response body returned by the API when a request fails.
 *
 * @param message a human-readable description of the error
 */
public record ErrorResponse(String message) {
}
