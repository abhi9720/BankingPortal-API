package com.webapp.bankingportal.dto;

public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.setMessage(message);
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    
}