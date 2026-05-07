package com.webapp.bankingportal.exception;

/**
 * Thrown when an authenticated user attempts an operation they are not authorized to perform
 * (e.g., wrong PIN, wrong password, no PIN set).
 *
 * <p>Mapped to HTTP 401 Unauthorized by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
