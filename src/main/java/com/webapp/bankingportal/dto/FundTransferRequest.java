package com.webapp.bankingportal.dto;


public class FundTransferRequest {
    private String sourceAccountNumber;
    private String sourceAccountPin;
    private String targetAccountNumber;
    private double amount;
    private String pin;
    
	public String getSourceAccountNumber() {
		return sourceAccountNumber;
	}
	public void setSourceAccountNumber(String sourceAccountNumber) {
		this.sourceAccountNumber = sourceAccountNumber;
	}
	public String getSourceAccountPin() {
		return sourceAccountPin;
	}
	public void setSourceAccountPin(String sourceAccountPin) {
		this.sourceAccountPin = sourceAccountPin;
	}
	public String getTargetAccountNumber() {
		return targetAccountNumber;
	}
	public void setTargetAccountNumber(String targetAccountNumber) {
		this.targetAccountNumber = targetAccountNumber;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
    

}
