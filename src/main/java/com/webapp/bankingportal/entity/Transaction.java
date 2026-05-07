package com.webapp.bankingportal.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;

/**
 * JPA entity that records a single financial transaction.
 *
 * <p>Transactions are created automatically by the account service during
 * deposits, withdrawals, and fund transfers. For unilateral operations
 * (deposit/withdrawal) only {@code sourceAccount} is populated; for
 * transfers both {@code sourceAccount} and {@code targetAccount} are set.</p>
 *
 * @see TransactionType
 */
@Entity
@Data
public class Transaction {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Monetary amount involved in the transaction. */
    private double amount;

    /** Category of the transaction (deposit, withdrawal, transfer, credit). */
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    /** Timestamp at which the transaction was recorded. */
    private Date transactionDate;

    /** Account from which funds originated (always set). */
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    /** Destination account for fund transfers; {@code null} for unilateral operations. */
    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

}
