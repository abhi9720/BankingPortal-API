package com.webapp.bankingportal.dto;

/**
 * Request body for a fund transfer between two accounts.
 *
 * @param sourceAccountNumber the account number from which funds are debited
 * @param targetAccountNumber the account number to which funds are credited
 * @param amount              the monetary amount to transfer (must be a positive multiple of 100, max 100,000)
 * @param pin                 the 4-digit PIN of the source account authorizing the transfer
 */
public record FundTransferRequest(String sourceAccountNumber, String targetAccountNumber, double amount, String pin) {
}
