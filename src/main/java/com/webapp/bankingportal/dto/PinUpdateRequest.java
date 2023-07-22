package com.webapp.bankingportal.dto;

public class PinUpdateRequest {
    private String accountNumber;
    private String oldPin;
    private String newPin;
    private String password;
    
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getOldPin() {
		return oldPin;
	}
	public void setOldPin(String oldPin) {
		this.oldPin = oldPin;
	}
	public String getNewPin() {
		return newPin;
	}
	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

    // Add getters and setters
}
