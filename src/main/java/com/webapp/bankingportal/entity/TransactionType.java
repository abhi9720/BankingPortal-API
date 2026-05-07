package com.webapp.bankingportal.entity;

/**
 * Enumeration of the transaction types supported by the banking portal.
 *
 * <p>This value is persisted as a string column in the {@code transaction} table.</p>
 */
public enum TransactionType {
    /** Funds removed from an account by the account holder. */
    CASH_WITHDRAWAL,
    /** Funds added to an account by the account holder. */
    CASH_DEPOSIT,
    /** Funds moved from one account to another. */
    CASH_TRANSFER,
    /** Credit applied to an account (e.g., interest or refund). */
    CASH_CREDIT
}
