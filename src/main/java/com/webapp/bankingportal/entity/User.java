package com.webapp.bankingportal.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a registered user of the banking portal.
 *
 * <p>A user holds personal contact information and is associated with exactly
 * one {@link Account} through a bidirectional one-to-one relationship.
 * Passwords are stored in BCrypt-hashed form; plain-text passwords must never
 * be persisted.</p>
 */
@Entity
@Data
public class User {

    /** Auto-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full name of the user. */
    @NotEmpty
    private String name;

    /** BCrypt-hashed password. Never stored in plain text. */
    @NotEmpty
    private String password;

    /** Unique email address used for login and notifications. */
    @Email
    @NotEmpty
    @Column(unique = true)
    private String email;

    /** ISO 3166-1 alpha-2 country code (e.g., "US", "IN"). Stored in uppercase. */
    @NotEmpty
    private String countryCode;

    /** Unique phone number including the national subscriber number. */
    @NotEmpty
    @Column(unique = true)
    private String phoneNumber;

    /** Residential or mailing address of the user. */
    @NotEmpty
    private String address;

    /** The bank account associated with this user (created on registration). */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

}
