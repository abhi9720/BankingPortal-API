package com.webapp.bankingportal.exception;

/**
 * Thrown when a requested resource (user, account, etc.) cannot be found in the database.
 *
 * <p>Mapped to HTTP 404 Not Found by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }
}
