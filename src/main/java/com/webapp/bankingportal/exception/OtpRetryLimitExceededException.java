package com.webapp.bankingportal.exception;

/**
 * Thrown when a user has requested too many OTPs within the allowed retry window and must wait
 * before requesting another.
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.
 * The message typically includes the number of minutes remaining before the user can try again.</p>
 */
public class OtpRetryLimitExceededException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message including the wait time.
     *
     * @param message the detail message (e.g., "OTP generation limit exceeded. Please try again after 8 minutes")
     */
    public OtpRetryLimitExceededException(String message) {
        super(message);
    }
}
