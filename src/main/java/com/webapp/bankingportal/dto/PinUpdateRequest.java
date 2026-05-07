package com.webapp.bankingportal.dto;

/**
 * Request body for updating an existing transaction PIN on an account.
 *
 * @param accountNumber the account number whose PIN is being updated
 * @param oldPin        the current 4-digit PIN to verify ownership
 * @param newPin        the new 4-digit numeric PIN to set
 * @param password      the account holder's login password for additional identity verification
 */
public record PinUpdateRequest(String accountNumber, String oldPin, String newPin, String password) {
}
