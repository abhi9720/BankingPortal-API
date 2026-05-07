package com.webapp.bankingportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.webapp.bankingportal.entity.User;

/**
 * Spring Data JPA repository for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique phone number.
     *
     * @param phoneNumber the phone number to search for
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Finds a user by the account number of their associated bank account.
     *
     * @param accountNumber the 6-character account number to search for
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByAccountAccountNumber(String accountNumber);
}
