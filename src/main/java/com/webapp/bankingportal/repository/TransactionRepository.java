package com.webapp.bankingportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.Transaction;

/**
 * Spring Data JPA repository for {@link Transaction} entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Returns all transactions in which the given account number appears as either
     * the source or the target account.
     *
     * @param sourceAccountNumber the account number to match against the source account
     * @param targetAccountNumber the account number to match against the target account
     *                            (pass the same value as {@code sourceAccountNumber} to retrieve
     *                            all transactions for a single account)
     * @return a list of matching {@link Transaction} records, possibly empty
     */
    List<Transaction> findBySourceAccount_AccountNumberOrTargetAccount_AccountNumber(
            String sourceAccountNumber, String targetAccountNumber);
}
