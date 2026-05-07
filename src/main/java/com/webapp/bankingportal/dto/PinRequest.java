package com.webapp.bankingportal.dto;

/**
 * Request body for creating a new transaction PIN on an account.
 *
 * @param accountNumber the account number for which the PIN is being created
 * @param pin           the desired 4-digit numeric PIN
 * @param password      the account holder's login password, used to verify identity before PIN creation
 */
public record PinRequest(String accountNumber, String pin, String password) {
}
