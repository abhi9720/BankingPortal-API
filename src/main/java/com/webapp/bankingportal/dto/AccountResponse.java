package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-only view of an {@link com.webapp.bankingportal.entity.Account Account} returned by the dashboard and
 * account endpoints.
 *
 * <p>Sensitive fields such as the PIN are intentionally excluded.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    /** The unique 6-digit account number. */
    private String accountNumber;

    /** Current balance of the account. */
    private double balance;

    /** Type of account (e.g., "Savings"). */
    private String accountType;

    /** Branch name associated with the account. */
    private String branch;

    /** IFSC code of the branch. */
    private String ifscCode;

    /**
     * Constructs an {@code AccountResponse} from a persisted {@link com.webapp.bankingportal.entity.Account Account} entity.
     *
     * @param account the source account entity
     */
    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.branch = account.getBranch();
        this.ifscCode = account.getIfscCode();
    }

}
