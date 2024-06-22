package com.webapp.bankingportal.exception;

public class InvalidPinException extends RuntimeException {

    public InvalidPinException(String message) {
        super(message);
    }
}
