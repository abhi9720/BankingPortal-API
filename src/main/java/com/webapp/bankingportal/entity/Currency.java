package com.webapp.bankingportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a currency that can be associated with an {@link Account}
 * (and optionally with individual {@link Transaction}s).
 *
 * <p>The {@link #code} is the ISO 4217 three-letter currency code
 * (e.g. {@code USD}, {@code EUR}, {@code INR}).</p>
 */
@Entity
@Table(name = "currency")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ISO 4217 currency code, e.g. {@code USD}. */
    @NotEmpty
    @Column(unique = true, nullable = false, length = 3)
    private String code;

    /** Human-readable name, e.g. {@code "US Dollar"}. */
    @NotEmpty
    @Column(nullable = false)
    private String name;

    /** Display symbol, e.g. {@code "$"}. */
    @Column(length = 8)
    private String symbol;
}

