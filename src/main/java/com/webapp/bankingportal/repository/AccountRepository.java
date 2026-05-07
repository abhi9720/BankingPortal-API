package com.webapp.bankingportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.Account;

/**
 * Spring Data JPA repository for {@link Account} entities.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by its unique account number.
     *
     * @param accountNumber the 6-character account number to search for
     * @return the matching {@link Account}, or {@code null} if not found
     */
    Account findByAccountNumber(String accountNumber);
}
