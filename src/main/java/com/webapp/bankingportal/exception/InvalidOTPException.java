package com.webapp.bankingportal.exception;

public class InvalidOTPException extends RuntimeException {

    public InvalidOTPException(String msg) {
        super(msg);
    }
}
