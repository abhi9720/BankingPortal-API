package com.webapp.bankingportal.service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

	   private final JavaMailSender mailSender;

	    @Autowired
	    public EmailServiceImpl(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    @Override
	    @Async
	    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
	        CompletableFuture<Void> future = new CompletableFuture<>();

	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setTo(to);
	            // No need to set the "from" address; it is automatically set by Spring Boot based on your properties
	            helper.setSubject(subject);
	            helper.setText(text, true); // Set the second parameter to true to send HTML content
	            mailSender.send(message);

	            future.complete(null); // Indicate that the email sending is successful
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            future.completeExceptionally(e); // Indicate that the email sending failed
	        }

	        return future;
	    }
	    
	    
	    public String getOtpLoginEmailTemplate(String name, String accountNumber, String otp) {
	        // Create the formatted email template with the provided values
	        String emailTemplate = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">"
	                + "<div style=\"margin:50px auto;width:70%;padding:20px 0\">"
	                + "<div style=\"border-bottom:1px solid #eee\">"
	                + "<a href=\"https://onestopbank.netlify.app/\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">OneStopBank</a>"
	                + "</div>"
	                + "<p style=\"font-size:1.1em\">Hi, " + name + "</p>"
	                + "<p style=\"font-size:0.9em;\">Account Number: " + accountNumber + "</p>"
	                + "<p>Thank you for choosing OneStopBank. Use the following OTP to complete your Log In procedures. OTP is valid for 5 minutes</p>"
	                + "<h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + otp + "</h2>"
	                + "<p style=\"font-size:0.9em;\">Regards,<br />OneStopBank</p>"
	                + "<hr style=\"border:none;border-top:1px solid #eee\" />"
	                + "<p>OneStopBank Inc</p>"
	                + "<p>1600 Amphitheatre Parkway</p>"
	                + "<p>California</p>"
	                + "</div>"
	                + "</div>";

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
