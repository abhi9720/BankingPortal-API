package com.webapp.bankingportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.Token;

/**
 * Spring Data JPA repository for JWT {@link Token} entities.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Finds a persisted token record by its JWT string value.
     *
     * @param token the raw JWT string
     * @return the matching {@link Token}, or {@code null} if not found
     */
    Token findByToken(String token);

    /**
     * Returns all tokens associated with a given account.
     *
     * @param account the account whose tokens are to be retrieved
     * @return an array of {@link Token} records (may be empty)
     */
    Token[] findAllByAccount(Account account);

    /**
     * Deletes the token record with the given JWT string.
     *
     * @param token the raw JWT string identifying the record to delete
     */
    void deleteByToken(String token);
}
