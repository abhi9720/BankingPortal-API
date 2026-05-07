package com.webapp.bankingportal.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a JWT session token issued to an account.
 *
 * <p>Tokens are persisted when a user logs in and are removed (invalidated)
 * on logout or expiry. Storing tokens in the database allows the application
 * to perform server-side revocation of JWTs.</p>
 */
@Entity
@NoArgsConstructor
@Data
public class Token {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Raw JWT string. Must be unique across all active tokens. */
    @NotEmpty
    @Column(unique = true)
    private String token;

    /** Timestamp when this token was issued. Defaults to the current time. */
    @NotNull
    private Date createdAt = new Date();

    /** Timestamp after which the token is considered expired. */
    @NotNull
    private Date expiryAt;

    /** The account to which this token belongs. */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * Creates a new Token with the given JWT string, expiry time, and associated account.
     *
     * @param token    the raw JWT string
     * @param expiryAt the expiry timestamp of the token
     * @param account  the account this token is associated with
     */
    public Token(String token, Date expiryAt, Account account) {
        this.token = token;
        this.expiryAt = expiryAt;
        this.account = account;
    }

}
