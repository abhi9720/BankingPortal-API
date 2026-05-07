package com.webapp.bankingportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity that stores an active one-time password (OTP) for an account.
 *
 * <p>Only one OTP record exists per account number at any given time.
 * The record is deleted once the OTP is consumed or expires. The
 * {@code generatedAt} field is used both to determine expiry and to track
 * the retry-limit window.</p>
 */
@Entity
@NoArgsConstructor
@Data
public class OtpInfo {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Account number for which this OTP was generated. Unique per account. */
    @Column(unique = true)
    private String accountNumber;

    /** The 6-digit OTP value (stored as plain text for fast lookup). */
    @Column
    private String otp;

    /** Timestamp when the OTP was generated or last refreshed. */
    @Column
    private LocalDateTime generatedAt;

    /**
     * Creates a new OtpInfo record.
     *
     * @param accountNumber the account number the OTP belongs to
     * @param otp           the generated OTP value
     * @param generatedAt   the timestamp at which the OTP was created
     */
    public OtpInfo(String accountNumber, String otp, LocalDateTime generatedAt) {
        this.accountNumber = accountNumber;
        this.otp = otp;
        this.generatedAt = generatedAt;
    }

}
