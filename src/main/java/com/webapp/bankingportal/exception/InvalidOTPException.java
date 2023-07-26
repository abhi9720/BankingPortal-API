package com.webapp.bankingportal.exception;

public class InvalidOTPException  extends RuntimeException  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9176543614742615822L;

	public InvalidOTPException(String msg) {
		super(msg);
	}

}
