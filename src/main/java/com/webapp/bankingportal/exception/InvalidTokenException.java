package com.webapp.bankingportal.exception;

/**
 * Checked exception thrown when a JWT is expired, malformed, has an invalid signature,
 * is not found in the database, or is otherwise unusable.
 *
 * <p>Mapped to HTTP 401 Unauthorized by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.
 * Declared as a checked exception ({@link Exception}) rather than a runtime exception
 * because callers must explicitly decide how to handle an invalid token.</p>
 */
public class InvalidTokenException extends Exception {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
