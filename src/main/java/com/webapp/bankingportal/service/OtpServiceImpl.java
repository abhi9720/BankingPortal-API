package com.webapp.bankingportal.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.OtpInfo;
import com.webapp.bankingportal.exception.AccountDoesNotExistException;
import com.webapp.bankingportal.exception.InvalidOtpException;
import com.webapp.bankingportal.exception.OtpRetryLimitExceededException;
import com.webapp.bankingportal.repository.OtpInfoRepository;
import com.webapp.bankingportal.util.ValidationUtil;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Default implementation of {@link OtpService}.
 *
 * <p>Manages OTP generation, caching of attempt counts, expiry checking,
 * and asynchronous email delivery. OTP generation is rate-limited:
 * after {@value #OTP_ATTEMPTS_LIMIT} attempts within a
 * {@value #OTP_RETRY_LIMIT_WINDOW_MINUTES}-minute window the user must wait
 * {@value #OTP_RESET_WAITING_TIME_MINUTES} minutes before retrying.</p>
 */
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    /** Maximum number of OTP generation attempts before the retry limit kicks in. */
    public static final int OTP_ATTEMPTS_LIMIT = 3;

    /** Number of minutes after which an unused OTP expires. */
    public static final int OTP_EXPIRY_MINUTES = 5;

    /** Cool-down period in minutes before OTP generation is allowed again after hitting the limit. */
    public static final int OTP_RESET_WAITING_TIME_MINUTES = 10;

    /** Sliding window in minutes within which OTP attempt counts are tracked. */
    public static final int OTP_RETRY_LIMIT_WINDOW_MINUTES = 15;

    private final CacheManager cacheManager;
    private final EmailService emailService;
    private final OtpInfoRepository otpInfoRepository;
    private final ValidationUtil validationUtil;

    private LocalDateTime otpLimitReachedTime = null;

    @Override
    public String generateOTP(String accountNumber) {
        if (!validationUtil.doesAccountExist(accountNumber)) {
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
            return generateNewOTP(accountNumber);
        }

        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo)) {
            return generateNewOTP(accountNumber);
        }

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
        incrementOtpAttempts(accountNumber);

        return existingOtpInfo.getOtp();
    }

    /**
     * Throws {@link OtpRetryLimitExceededException} if the account has exceeded
     * the OTP generation limit within the current retry window.
     *
     * @param otpInfo the existing OTP record for the account
     */
    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        val now = LocalDateTime.now();

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
        }

        val waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);

        throw new OtpRetryLimitExceededException(
                String.format(ApiMessages.OTP_GENERATION_LIMIT_EXCEEDED.getMessage(), waitingMinutes));
    }

    /**
     * Returns {@code true} if the account has reached the maximum number of OTP attempts
     * within the retry window and the cool-down period has not yet elapsed.
     *
     * @param otpInfo the OTP record for the account
     * @return {@code true} if the retry limit is exceeded, {@code false} otherwise
     */
    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
        val attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        val now = LocalDateTime.now();

        return otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
    }

    /**
     * Returns {@code true} if the cool-down period since the limit was first reached has elapsed.
     *
     * @return {@code true} if enough time has passed to reset the OTP attempt counter
     */
    private boolean isOtpResetWaitingTimeExceeded() {
        val now = LocalDateTime.now();
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }

    /**
     * Increments the cached OTP attempt counter for the given account by one.
     *
     * @param accountNumber the account number whose attempt counter should be incremented
     * @throws AccountDoesNotExistException if the account is not found
     */
    private void incrementOtpAttempts(String accountNumber) {
        if (!validationUtil.doesAccountExist(accountNumber)) {
            throw new AccountDoesNotExistException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
    }

    /**
     * Resets the cached OTP attempt counter for the given account to zero and
     * clears the limit-reached timestamp.
     *
     * @param accountNumber the account number whose attempt counter should be reset
     */
    private void resetOtpAttempts(String accountNumber) {
        otpLimitReachedTime = null;
        val cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
    }

    /**
     * Returns the current OTP attempt count for the given account from the cache.
     * Returns {@code 0} if no entry exists or the cache is unavailable.
     *
     * @param accountNumber the account number to look up
     * @return the current attempt count, or {@code 0}
     */
    private int getOtpAttempts(String accountNumber) {
        var otpAttempts = 0;
        val cache = cacheManager.getCache("otpAttempts");
        if (cache == null) {
            return otpAttempts;
        }

        val value = cache.get(accountNumber, Integer.class);
        if (value != null) {
            otpAttempts = value;
        }

        return otpAttempts;
    }

    /**
     * Generates a new random 6-digit OTP, persists it for the given account, and returns it.
     *
     * @param accountNumber the account number for which the OTP is generated
     * @return the newly generated 6-digit OTP string
     */
    private String generateNewOTP(String accountNumber) {
        val random = new Random();
        val otpValue = 100_000 + random.nextInt(900_000);
        val otp = String.valueOf(otpValue);

        otpInfoRepository.save(new OtpInfo(accountNumber, otp, LocalDateTime.now()));

        return otp;
    }

    @Override
    public CompletableFuture<Void> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
        val emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);
        return emailService.sendEmail(email, ApiMessages.EMAIL_SUBJECT_OTP.getMessage(), emailText);
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
        val otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
            throw new InvalidOtpException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }

        return !isOtpExpired(otpInfo);
    }

    /**
     * Checks whether an OTP has exceeded its validity period and deletes the record if expired.
     *
     * @param otpInfo the OTP record to check
     * @return {@code true} if the OTP has expired (and was deleted), {@code false} if still valid
     */
    private boolean isOtpExpired(OtpInfo otpInfo) {
        val now = LocalDateTime.now();
        val generatedAt = otpInfo.getGeneratedAt();
        val expired = generatedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
        if (expired) {
            otpInfoRepository.delete(otpInfo);
        }

        return expired;
    }

}
