package com.webapp.bankingportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.PasswordResetToken;
import com.webapp.bankingportal.entity.User;

/**
 * Spring Data JPA repository for {@link PasswordResetToken} entities.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a password-reset token record by its UUID string value.
     *
     * @param token the token string to search for
     * @return an {@link Optional} containing the token record, or empty if not found
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Finds the password-reset token currently associated with a given user.
     *
     * @param user the user whose reset token is requested
     * @return the active {@link PasswordResetToken} for the user, or {@code null} if none exists
     */
    PasswordResetToken findByUser(User user);

    /**
     * Deletes the password-reset token record identified by the given string.
     *
     * @param token the token string identifying the record to delete
     */
    void deleteByToken(String token);
}
