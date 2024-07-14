package com.webapp.bankingportal.dto;

public record AmountRequest(String accountNumber, String pin, double amount) {
}
