package com.webapp.bankingportal.exception;

public class AccountDoesNotExists extends RuntimeException {
	
	public AccountDoesNotExists(String message) {
		super(message);
	}

}
