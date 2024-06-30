package com.webapp.bankingportal.dto;

public class OtpVerificationRequestv2 {
	private String identifier; // Can be either email or account number
	private String otp;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
}
