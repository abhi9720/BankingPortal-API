package com.webapp.bankingportal.exception;

public class OtpRetryLimitExceededException extends RuntimeException {
    public OtpRetryLimitExceededException(String message) {
        super(message);
    }
}
