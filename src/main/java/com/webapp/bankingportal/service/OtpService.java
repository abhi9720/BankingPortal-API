package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

/**
 * Service contract for generating, sending, and validating one-time passwords (OTPs).
 */
public interface OtpService {

    /**
     * Generates a 6-digit OTP for the given account and stores it in the database.
     *
     * <p>If a valid, unexpired OTP already exists for the account, the existing OTP
     * is refreshed (its timestamp is updated) and returned instead of creating a new one.
     * OTP generation is rate-limited; an exception is thrown if the retry limit is exceeded.</p>
     *
     * @param accountNumber the account number for which an OTP is being generated
     * @return the 6-digit OTP string
     * @throws com.webapp.bankingportal.exception.AccountDoesNotExistException   if the account is not found
     * @throws com.webapp.bankingportal.exception.OtpRetryLimitExceededException if too many OTPs have been requested
     */
    String generateOTP(String accountNumber);

    /**
     * Sends the OTP to the specified email address asynchronously using an HTML email template.
     *
     * @param email         the recipient's email address
     * @param name          the recipient's display name used in the email greeting
     * @param accountNumber the account number (partially masked in the email body)
     * @param otp           the OTP value to include in the email
     * @return a {@link CompletableFuture} that completes when the email has been sent,
     *         or completes exceptionally on delivery failure
     */
    CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp);

    /**
     * Validates that the given OTP matches the stored value for the account and has not expired.
     *
     * @param accountNumber the account number whose OTP is being validated
     * @param otp           the OTP value to check
     * @return {@code true} if the OTP is correct and still valid
     * @throws com.webapp.bankingportal.exception.InvalidOtpException if no matching OTP record is found
     */
    boolean validateOTP(String accountNumber, String otp);

}
