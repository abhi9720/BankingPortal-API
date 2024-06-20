package com.webapp.bankingportal.exception;

public class InvalidAmountException extends RuntimeException {
    
    public InvalidAmountException(String message) {
        super(message);
    }
}
