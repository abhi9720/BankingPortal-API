package com.webapp.bankingportal.exception;

/**
 * Thrown when a PIN fails format validation (e.g., not 4 digits) during creation or update.
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class InvalidPinException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public InvalidPinException(String message) {
        super(message);
    }
}
