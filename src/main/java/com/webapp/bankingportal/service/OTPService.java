package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

public interface OTPService {

	String generateOTP(String accountNumber);

	public CompletableFuture<Boolean> sendOTPByEmail(String email,String name,String accountNumber, String otp) ;	
	public boolean validateOTP(String accountNumber, String otp);

}
