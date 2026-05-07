package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-only projection of a {@link com.webapp.bankingportal.entity.User User} and their associated account,
 * returned by registration, update, and dashboard endpoints.
 *
 * <p>Sensitive fields such as the password and PIN are intentionally excluded.</p>
 */
@NoArgsConstructor
@Data
public class UserResponse {

    /** Full name of the user. */
    private String name;

    /** Email address of the user. */
    private String email;

    /** ISO 3166-1 alpha-2 country code (e.g., "US"). */
    private String countryCode;

    /** Phone number of the user. */
    private String phoneNumber;

    /** Residential address of the user. */
    private String address;

    /** The unique 6-digit account number linked to this user. */
    private String accountNumber;

    /** IFSC code of the account's branch. */
    private String ifscCode;

    /** Branch name of the account. */
    private String branch;

    /** Account type (e.g., "Savings"). */
    private String accountType;

    /**
     * Constructs a {@code UserResponse} by projecting fields from a {@link com.webapp.bankingportal.entity.User User} entity.
     *
     * @param user the source user entity (must have a non-null associated account)
     */
    public UserResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.countryCode = user.getCountryCode();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.accountNumber = user.getAccount().getAccountNumber();
        this.ifscCode = user.getAccount().getIfscCode();
        this.branch = user.getAccount().getBranch();
        this.accountType = user.getAccount().getAccountType();
    }

}
