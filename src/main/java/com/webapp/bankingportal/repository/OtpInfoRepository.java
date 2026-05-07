package com.webapp.bankingportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.OtpInfo;

/**
 * Spring Data JPA repository for {@link OtpInfo} entities.
 */
@Repository
public interface OtpInfoRepository extends JpaRepository<OtpInfo, Long> {

    /**
     * Finds an OTP record matching both the account number and the OTP value.
     * Used during OTP validation.
     *
     * @param accountNumber the account number the OTP belongs to
     * @param otp           the OTP value to match
     * @return the matching {@link OtpInfo}, or {@code null} if not found
     */
    OtpInfo findByAccountNumberAndOtp(String accountNumber, String otp);

    /**
     * Finds the active OTP record for a given account number.
     *
     * @param accountNumber the account number to look up
     * @return the active {@link OtpInfo} record, or {@code null} if none exists
     */
    OtpInfo findByAccountNumber(String accountNumber);
}
