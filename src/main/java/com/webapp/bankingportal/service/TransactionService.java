package com.webapp.bankingportal.service;

import java.util.List;

import com.webapp.bankingportal.dto.TransactionDTO;

/**
 * Service contract for retrieving transaction history and delivering bank statements.
 */
public interface TransactionService {

    /**
     * Returns a list of all transactions in which the given account was either
     * the source or the target, sorted in reverse chronological order.
     *
     * @param accountNumber the account number whose transactions are to be fetched
     * @return a non-null, possibly empty list of {@link TransactionDTO} objects
     */
    List<TransactionDTO> getAllTransactionsByAccountNumber(String accountNumber);

    /**
     * Sends a plain-text bank statement for the given account to the account
     * holder's registered email address.
     *
     * @param accountNumber the account number whose statement is to be emailed
     * @throws IllegalArgumentException if {@code accountNumber} is null or blank
     */
    void sendBankStatementByEmail(String accountNumber);

}
