package com.webapp.bankingportal.dto;

public class LoginRequest {
    private String accountNumber;
    private String password;
    private boolean useOtp;
    
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isUseOtp() {
		return useOtp;
	}
	public void setUseOtp(boolean useOtp) {
		this.useOtp = useOtp;
	}

    
}