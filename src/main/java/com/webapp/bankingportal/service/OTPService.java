package com.webapp.bankingportal.service;

public interface OTPService {

	String generateOTP(String accountNumber);

	public boolean sendOTPByEmail(String email,String name,String accountNumber, String otp) ;	
	public boolean validateOTP(String accountNumber, String otp);

}
