package com.webapp.bankingportal.exception;

/**
 * Thrown when a password-reset operation fails due to a persistence or unexpected error.
 *
 * <p>Mapped to HTTP 500 Internal Server Error by
 * {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class PasswordResetException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public PasswordResetException(String message) {
        super(message);
    }

    /**
     * Constructs the exception with a descriptive message and the underlying cause.
     *
     * @param message the detail message
     * @param cause   the exception that triggered this failure
     */
    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
