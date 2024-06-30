package com.webapp.bankingportal.dto;

public class OtpRequestv2 {
	private String identifier; // Can be either email or account number

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
