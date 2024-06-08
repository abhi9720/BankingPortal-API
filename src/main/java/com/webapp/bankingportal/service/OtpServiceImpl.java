package com.webapp.bankingportal.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.OtpInfo;
import com.webapp.bankingportal.exception.AccountDoesNotExistException;
import com.webapp.bankingportal.exception.InvalidOtpException;
import com.webapp.bankingportal.exception.OtpRetryLimitExceededException;
import com.webapp.bankingportal.repository.OtpInfoRepository;

@Service
public class OtpServiceImpl implements OtpService {

    public static final int OTP_ATTEMPTS_LIMIT = 3;
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int OTP_RESET_WAITING_TIME_MINUTES = 10;
    public static final int OTP_RETRY_LIMIT_WINDOW_MINUTES = 15;

    private LocalDateTime otpLimitReachedTime = null;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpInfoRepository otpInfoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);

    @Override
    public String generateOTP(String accountNumber) {
        if (!userService.doesAccountExist(accountNumber)) {
            throw new AccountDoesNotExistException("Account does not exist");
        }

        OtpInfo existingOtpInfo = otpInfoRepository.findByAccountNumber(accountNumber);
        if (existingOtpInfo == null) {
            incrementOtpAttempts(accountNumber);
            return generateNewOTP(accountNumber);
        }

        validateOtpWithinRetryLimit(existingOtpInfo);

        if (isOtpExpired(existingOtpInfo.getGeneratedAt())) {
            otpInfoRepository.delete(existingOtpInfo);
            return generateNewOTP(accountNumber);
        }

        // Existing OTP is not expired
        existingOtpInfo.setGeneratedAt(LocalDateTime.now());
        incrementOtpAttempts(accountNumber);

        return existingOtpInfo.getOtp();
    }

    private void validateOtpWithinRetryLimit(OtpInfo otpInfo) {
        if (!isOtpRetryLimitExceeded(otpInfo)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (otpLimitReachedTime == null) {
            otpLimitReachedTime = now;
        }

        long waitingMinutes = OTP_RESET_WAITING_TIME_MINUTES - otpLimitReachedTime.until(now, ChronoUnit.MINUTES);

        throw new OtpRetryLimitExceededException(
                "OTP generation limit exceeded. Please try again after " + waitingMinutes + " minutes");
    }

    private boolean isOtpRetryLimitExceeded(OtpInfo otpInfo) {
        int attempts = getOtpAttempts(otpInfo.getAccountNumber());
        if (attempts < OTP_ATTEMPTS_LIMIT) {
            return false;
        }

        if (isOtpResetWaitingTimeExceeded()) {
            resetOtpAttempts(otpInfo.getAccountNumber());
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        return otpInfo.getGeneratedAt().isAfter(now.minusMinutes(OTP_RETRY_LIMIT_WINDOW_MINUTES));
    }

    private boolean isOtpResetWaitingTimeExceeded() {
        LocalDateTime now = LocalDateTime.now();
        return otpLimitReachedTime != null
                && otpLimitReachedTime.isBefore(now.minusMinutes(OTP_RESET_WAITING_TIME_MINUTES));
    }

    private void incrementOtpAttempts(String accountNumber) {
        if (!userService.doesAccountExist(accountNumber)) {
            throw new AccountDoesNotExistException("Account does not exist");
        }

        Cache cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, getOtpAttempts(accountNumber) + 1);
        }
    }

    private void resetOtpAttempts(String accountNumber) {
        otpLimitReachedTime = null;
        Cache cache = cacheManager.getCache("otpAttempts");
        if (cache != null) {
            cache.put(accountNumber, 0);
        }
    }

    private int getOtpAttempts(String accountNumber) {
        int otpAttempts = 0;
        Cache cache = cacheManager.getCache("otpAttempts");
        if (cache == null) {
            return otpAttempts;
        }

        Integer value = cache.get(accountNumber, Integer.class);
        if (value != null) {
            otpAttempts = value;
        }

        return otpAttempts;
    }

    private String generateNewOTP(String accountNumber) {
        Random random = new Random();
        int otpValue = 100_000 + random.nextInt(900_000);
        String otp = String.valueOf(otpValue);

        // Save the new OTP information in the database
        OtpInfo otpInfo = new OtpInfo();
        otpInfo.setAccountNumber(accountNumber);
        otpInfo.setOtp(otp);
        otpInfo.setGeneratedAt(LocalDateTime.now());
        otpInfoRepository.save(otpInfo);

        return otp;
    }

    @Override
    public CompletableFuture<Boolean> sendOTPByEmail(String email, String name, String accountNumber, String otp) {
        // Compose the email content
        String subject = "OTP Verification";
        String emailText = emailService.getOtpLoginEmailTemplate(name, "xxx" + accountNumber.substring(3), otp);

        CompletableFuture<Void> emailSendingFuture = emailService.sendEmail(email, subject, emailText);

        return emailSendingFuture.thenApplyAsync(result -> true).exceptionally(ex -> {
            logger.error("Failed to send OTP to user: {}", email, ex);
            return false;
        });
    }

    @Override
    public boolean validateOTP(String accountNumber, String otp) {
        OtpInfo otpInfo = otpInfoRepository.findByAccountNumberAndOtp(accountNumber, otp);
        if (otpInfo == null) {
            throw new InvalidOtpException("Invalid OTP");
        }
        otpInfoRepository.delete(otpInfo);

        return !isOtpExpired(otpInfo.getGeneratedAt());
    }

    private boolean isOtpExpired(LocalDateTime otpGeneratedAt) {
        LocalDateTime now = LocalDateTime.now();
        return otpGeneratedAt.isBefore(now.minusMinutes(OTP_EXPIRY_MINUTES));
    }
}
