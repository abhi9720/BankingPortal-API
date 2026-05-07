package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;

/**
 * Service contract for account management and financial transaction operations.
 *
 * <p>Provides operations for account creation, PIN management, and fund
 * movements (deposit, withdrawal, transfer). All methods that require user
 * authorization validate the provided PIN before executing.</p>
 */
public interface AccountService {

    /**
     * Creates a new bank account for the given user with a zero balance.
     *
     * @param user the user for whom the account is being created
     * @return the newly created and persisted {@link Account}
     */
    Account createAccount(User user);

    /**
     * Checks whether a transaction PIN has been created for the given account.
     *
     * @param accountNumber the account number to check
     * @return {@code true} if a PIN exists, {@code false} otherwise
     * @throws com.webapp.bankingportal.exception.NotFoundException if no account matches {@code accountNumber}
     */
    boolean isPinCreated(String accountNumber);

    /**
     * Creates a new 4-digit transaction PIN for an account after verifying the
     * account holder's password.
     *
     * @param accountNumber the account number on which to create the PIN
     * @param password      the account holder's login password for identity verification
     * @param pin           the 4-digit numeric PIN to set
     * @throws com.webapp.bankingportal.exception.UnauthorizedException if the password is incorrect or a PIN already exists
     * @throws com.webapp.bankingportal.exception.InvalidPinException   if the PIN format is invalid
     */
    void createPin(String accountNumber, String password, String pin);

    /**
     * Updates the transaction PIN on an account after verifying both the old PIN and
     * the account holder's password.
     *
     * @param accountNumber the account number whose PIN is being updated
     * @param oldPIN        the current PIN to verify
     * @param password      the account holder's login password for additional verification
     * @param newPIN        the new 4-digit numeric PIN to set
     * @throws com.webapp.bankingportal.exception.UnauthorizedException if the password or old PIN is incorrect
     * @throws com.webapp.bankingportal.exception.InvalidPinException   if the new PIN format is invalid
     */
    void updatePin(String accountNumber, String oldPIN, String password, String newPIN);

    /**
     * Deposits a cash amount into an account after verifying the PIN.
     *
     * @param accountNumber the target account number
     * @param pin           the 4-digit PIN authorizing the deposit
     * @param amount        the amount to deposit (must be positive, a multiple of 100, and at most 100,000)
     * @throws com.webapp.bankingportal.exception.InvalidAmountException if the amount violates the deposit rules
     * @throws com.webapp.bankingportal.exception.UnauthorizedException  if the PIN is invalid
     */
    void cashDeposit(String accountNumber, String pin, double amount);

    /**
     * Withdraws a cash amount from an account after verifying the PIN and checking the balance.
     *
     * @param accountNumber the source account number
     * @param pin           the 4-digit PIN authorizing the withdrawal
     * @param amount        the amount to withdraw (must be positive, a multiple of 100, and at most 100,000)
     * @throws com.webapp.bankingportal.exception.InvalidAmountException      if the amount violates the withdrawal rules
     * @throws com.webapp.bankingportal.exception.InsufficientBalanceException if the account does not have enough funds
     * @throws com.webapp.bankingportal.exception.UnauthorizedException        if the PIN is invalid
     */
    void cashWithdrawal(String accountNumber, String pin, double amount);

    /**
     * Transfers funds from one account to another after verifying the source account PIN.
     *
     * @param sourceAccountNumber the account number to debit
     * @param targetAccountNumber the account number to credit
     * @param pin                 the 4-digit PIN of the source account
     * @param amount              the amount to transfer (must be positive, a multiple of 100, and at most 100,000)
     * @throws com.webapp.bankingportal.exception.FundTransferException        if the source and target accounts are the same
     * @throws com.webapp.bankingportal.exception.NotFoundException             if the target account does not exist
     * @throws com.webapp.bankingportal.exception.InsufficientBalanceException if the source account lacks sufficient funds
     * @throws com.webapp.bankingportal.exception.UnauthorizedException        if the PIN is invalid
     */
    void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount);

}
