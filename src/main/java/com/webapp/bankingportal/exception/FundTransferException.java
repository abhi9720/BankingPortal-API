package com.webapp.bankingportal.exception;

/**
 * Thrown when a fund-transfer operation cannot be completed due to a business rule violation
 * (e.g., the source and target accounts are the same).
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class FundTransferException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public FundTransferException(String message) {
        super(message);
    }

}
