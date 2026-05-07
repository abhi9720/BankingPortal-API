package com.webapp.bankingportal.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * JPA entity representing a bank account in the system.
 *
 * <p>Each account is linked one-to-one with a {@link User} and holds the
 * account balance, type, branch details, and the hashed PIN used to
 * authorize transactions. A single account may have many associated
 * {@link Token} records (JWT tokens issued during active sessions).</p>
 */
@Entity
@Data
public class Account {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique 6-digit account number assigned on account creation. */
    @NotEmpty
    @Column(unique = true)
    private String accountNumber;

    /** Type of account (e.g., "Savings"). Defaults to {@code "Savings"}. */
    @NotEmpty
    private String accountType = "Savings";

    /** Current status of the account (e.g., active, suspended). */
    private String accountStatus;

    /** Current account balance. */
    private double balance;

    /** Branch name associated with this account. Defaults to {@code "NIT"}. */
    private String branch = "NIT";

    /** IFSC code of the branch. Defaults to {@code "NIT001"}. */
    private String ifscCode = "NIT001";

    /** BCrypt-hashed 4-digit PIN used to authorize transactions. */
    private String Pin;

    /** The user who owns this account. */
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** JWT tokens issued to the account's user across active sessions. */
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Token> tokens = new ArrayList<>();

}
