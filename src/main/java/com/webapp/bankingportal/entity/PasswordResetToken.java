package com.webapp.bankingportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JPA entity representing a single-use token used to authorize a password reset.
 *
 * <p>Tokens are issued after OTP verification and are valid for
 * {@code AuthServiceImpl#EXPIRATION_HOURS} hours. Once used they are deleted.
 * Implements {@link Serializable} to support sequence-generator strategies
 * across JPA providers.</p>
 */
@Entity
@Table(name = "passwordresettoken")
@Data
@NoArgsConstructor
public class PasswordResetToken implements Serializable {

    /** Auto-generated primary key using a database sequence. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passwordresettoken_sequence")
    @SequenceGenerator(name = "passwordresettoken_sequence", sequenceName = "passwordresettoken_sequence", allocationSize = 100)
    private Long id;

    /** Opaque UUID token string sent to the user for password reset. */
    @Column(nullable = false, unique = true)
    private String token;

    /** The user who requested the password reset. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Timestamp after which this token is considered expired. */
    @Column(nullable = false)
    private LocalDateTime expiryDateTime;

    /**
     * Creates a new PasswordResetToken.
     *
     * @param token          the opaque UUID reset token string
     * @param user           the user requesting the password reset
     * @param expiryDateTime the point in time at which the token expires
     */
    public PasswordResetToken(String token, User user, LocalDateTime expiryDateTime) {
        this.token = token;
        this.user = user;
        this.expiryDateTime = expiryDateTime;
    }

    /**
     * Returns {@code true} if the token has not yet passed its expiry date/time.
     *
     * @return {@code true} if the token is still valid, {@code false} otherwise
     */
    public boolean isTokenValid() {
        return getExpiryDateTime().isAfter(LocalDateTime.now());
    }

}
