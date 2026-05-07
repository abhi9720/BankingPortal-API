package com.webapp.bankingportal.exception;

/**
 * Thrown when an OTP submitted by the user does not match the stored value or is not found.
 *
 * <p>Mapped to HTTP 401 Unauthorized by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class InvalidOtpException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param msg the detail message
     */
    public InvalidOtpException(String msg) {
        super(msg);
    }
}
