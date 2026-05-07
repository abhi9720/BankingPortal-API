package com.webapp.bankingportal.exception;

/**
 * Thrown when a transaction amount violates a business rule (e.g., negative, not a multiple of
 * 100, or exceeds the maximum limit).
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class InvalidAmountException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}
