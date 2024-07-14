package com.webapp.bankingportal.dto;

public record PinUpdateRequest(String accountNumber, String oldPin, String newPin, String password) {
}
