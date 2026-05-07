package com.webapp.bankingportal.service;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.UserResponse;

/**
 * Service contract for retrieving user and account summary data for the dashboard.
 */
public interface DashboardService {

    /**
     * Returns a read-only view of the user associated with the given account number.
     *
     * @param accountNumber the account number whose owner details are requested
     * @return a {@link UserResponse} containing user and account information
     * @throws com.webapp.bankingportal.exception.NotFoundException if no user is linked to the account number
     */
    UserResponse getUserDetails(String accountNumber);

    /**
     * Returns a read-only view of the account identified by the given account number.
     *
     * @param accountNumber the account number to look up
     * @return an {@link AccountResponse} containing account details
     * @throws com.webapp.bankingportal.exception.NotFoundException if no account matches the given number
     */
    AccountResponse getAccountDetails(String accountNumber);
}