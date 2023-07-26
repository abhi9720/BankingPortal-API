package com.webapp.bankingportal.service;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

	   private final JavaMailSender mailSender;

	    @Autowired
	    public EmailServiceImpl(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    @Override
	    public void sendEmail(String to, String subject, String text) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setTo(to);
	            // No need to set the "from" address; it is automatically set by Spring Boot based on your properties
	            helper.setSubject(subject);
	            helper.setText(text, true); // Set the second parameter to true to send HTML content
	            mailSender.send(message);
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public String getOtpLoginEmailTemplate(String name ,String accountNumber, String otp) {
	        // Create the formatted email template with the provided values
	    	String emailTemplate = "<html>"
	    		    + "<head>"
	    		    + "<title>OTP Verification</title>"
	    		    + "</head>"
	    		    + "<body>"
	    		    + "<h3 style='color: #0066cc;'>Hello, "+name+"</h3>"
	    		    + "<p>You are receiving this email because you requested to login using OTP.</p>"
	    		    + "<table style='border-collapse: collapse;'>"
	    		    + "<tr>"
	    		    + "<th style='border: 1px solid black; padding: 10px;'>Account Number</th>"
	    		    + "<td style='border: 1px solid black; padding: 10px;'>"+accountNumber+"</td>"
	    		    + "</tr>"
	    		    + "<tr>"
	    		    + "<th style='border: 1px solid black; padding: 10px;'>OTP</th>"
	    		    + "<td style='border: 1px solid black; padding: 10px; background-color: #f2f2f2; font-weight: bold;'>"+otp+"</td>"
	    		    + "</tr>"
	    		    + "</table>"
	    		    + "<p>Please use this OTP to log in.</p>"
	    		    + "<p>Best regards,<br>Your Banking Portal Team</p>"
	    		    + "<p><a href='https://onestopbank.netlify.app/' style='background-color: #0066cc; color: white; padding: 10px 20px; text-decoration: none;'>Visit OneStopBank</a></p>"
	    		    + "</body>"
	    		    + "</html>";

	        return emailTemplate;
	    }

	    
	    public void sendEmailWithAttachment(String to, String subject, String text, String attachmentFilePath) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(text, true); // Set the second parameter to true to send HTML content

	            // Add an attachment to the email
	            File attachmentFile = new File(attachmentFilePath);
	            helper.addAttachment(attachmentFile.getName(), attachmentFile);

	            mailSender.send(message);
	        } catch (MessagingException  e) {
	            e.printStackTrace();
	        }
	    }

}
