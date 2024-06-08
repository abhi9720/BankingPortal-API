package com.webapp.bankingportal;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.service.EmailService;
import com.webapp.bankingportal.service.OtpServiceImpl;

@Service
public class EmailServiceMock implements EmailService {

    @Override
    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null); // Indicate that the email sending is successful

        return future;
    }

    @Override
    public String getOtpLoginEmailTemplate(String name, String accountNumber, String otp) {
        // Create the formatted email template with the provided values
        String emailTemplate = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">"
                + "<div style=\"margin:0 auto;width:70%;padding:20px 0\">"
                + "<div style=\"border-bottom:1px solid #eee\">"
                + "<a href=\"https://onestopbank.netlify.app/\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">OneStopBank</a>"
                + "</div>"
                + "<p style=\"font-size:1.1em\">Hi, " + name + "</p>"
                + "<p style=\"font-size:0.9em;\">Account Number: " + accountNumber + "</p>"
                + "<p>Thank you for choosing OneStopBank. Use the following OTP to complete your Log In procedures. OTP is valid for "
                + OtpServiceImpl.OTP_EXPIRY_MINUTES + " minutes</p>"
                + "<h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">"
                + otp + "</h2>"
                + "<p style=\"font-size:0.9em;\">Regards,<br />OneStopBank</p>"
                + "<hr style=\"border:none;border-top:1px solid #eee\" />"
                + "<p>OneStopBank Inc</p>"
                + "<p>1600 Amphitheatre Parkway</p>"
                + "<p>California</p>"
                + "</div>"
                + "</div>";

        return emailTemplate;
    }
}
