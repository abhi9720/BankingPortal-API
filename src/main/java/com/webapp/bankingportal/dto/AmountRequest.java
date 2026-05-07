package com.webapp.bankingportal.dto;

/**
 * Request body for cash deposit and cash withdrawal operations.
 *
 * @param accountNumber the account number on which the operation is performed
 * @param pin           the 4-digit PIN authorizing the transaction
 * @param amount        the monetary amount to deposit or withdraw (must be a positive multiple of 100, max 100,000)
 */
public record AmountRequest(String accountNumber, String pin, double amount) {
}
