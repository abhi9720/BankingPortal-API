package com.webapp.bankingportal.service;



public interface EmailService {
	public void sendEmail(String to, String subject, String text);
    public String getOtpLoginEmailTemplate(String name,String accountNumber, String otp) ;
}
