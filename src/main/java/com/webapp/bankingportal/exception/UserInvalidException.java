package com.webapp.bankingportal.exception;

/**
 * Thrown when user-supplied registration or profile data fails validation
 * (e.g., invalid email format, duplicate phone number, weak password).
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class UserInvalidException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public UserInvalidException(String message) {
        super(message);
    }
}
