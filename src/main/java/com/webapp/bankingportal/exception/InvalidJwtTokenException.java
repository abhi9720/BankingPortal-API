package com.webapp.bankingportal.exception;

public class InvalidJwtTokenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6859778110195654839L;

	public InvalidJwtTokenException(String message) {
        super(message);
    }
}
