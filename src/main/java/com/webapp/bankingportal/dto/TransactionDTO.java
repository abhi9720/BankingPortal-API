package com.webapp.bankingportal.dto;

import java.util.Date;

import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

/**
 * Data transfer object representing a single transaction in the transaction history.
 *
 * <p>Returned by the transaction list endpoint. Account entity references are
 * replaced by their string account numbers to avoid exposing sensitive data.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    /** Unique identifier of the transaction. */
    private Long id;

    /** Monetary amount involved in the transaction. */
    private double amount;

    /** Category of the transaction (deposit, withdrawal, transfer, credit). */
    private TransactionType transactionType;

    /** Timestamp when the transaction was recorded. */
    private Date transactionDate;

    /** Account number of the originating account. */
    private String sourceAccountNumber;

    /**
     * Account number of the receiving account, or {@code "N/A"} for
     * unilateral operations such as deposits and withdrawals.
     */
    private String targetAccountNumber;

    /**
     * Constructs a {@code TransactionDTO} from a {@link Transaction} entity,
     * extracting account numbers from the associated account objects.
     *
     * @param transaction the source transaction entity
     */
    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.transactionType = transaction.getTransactionType();
        this.transactionDate = transaction.getTransactionDate();
        this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();

        val targetAccount = transaction.getTargetAccount();
        var targetAccountNumber = "N/A";
        if (targetAccount != null) {
            targetAccountNumber = targetAccount.getAccountNumber();
        }

        this.targetAccountNumber = targetAccountNumber;
    }

}
