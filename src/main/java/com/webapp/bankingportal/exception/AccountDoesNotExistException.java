package com.webapp.bankingportal.exception;

/**
 * Thrown when an operation references an account number that does not exist in the database.
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class AccountDoesNotExistException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public AccountDoesNotExistException(String message) {
        super(message);
    }
}
