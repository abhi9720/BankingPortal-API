package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

/**
 * Service contract for composing and sending HTML emails.
 */
public interface EmailService {

    /**
     * Sends an HTML email to the specified recipient asynchronously.
     *
     * @param to      the recipient's email address
     * @param subject the email subject line
     * @param text    the HTML body of the email
     * @return a {@link CompletableFuture} that completes when delivery succeeds,
     *         or completes exceptionally on failure
     */
    @Async
    CompletableFuture<Void> sendEmail(String to, String subject, String text);

    /**
     * Builds the HTML login-notification email body informing the user of a
     * new login attempt.
     *
     * @param name          the recipient's display name
     * @param loginTime     human-readable timestamp of the login attempt
     * @param loginLocation human-readable city and country string, or {@code "Unknown"}
     * @return the HTML email body as a string
     */
    String getLoginEmailTemplate(String name, String loginTime, String loginLocation);

    /**
     * Builds the HTML OTP email body containing the one-time password.
     *
     * @param name          the recipient's display name
     * @param accountNumber the partially masked account number shown in the email
     * @param otp           the 6-digit OTP to include
     * @return the HTML email body as a string
     */
    String getOtpLoginEmailTemplate(String name, String accountNumber, String otp);

    /**
     * Builds the HTML bank statement email body containing the plain-text statement.
     *
     * @param name          the recipient's display name
     * @param statementText the plain-text bank statement content
     * @return the HTML email body as a string
     */
    String getBankStatementEmailTemplate(String name, String statementText);
}
