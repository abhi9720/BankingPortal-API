package com.webapp.bankingportal.exception;

/**
 * Thrown when a withdrawal or transfer is requested for an amount that exceeds the account balance.
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
