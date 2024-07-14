package com.webapp.bankingportal.dto;

public record PinRequest(String accountNumber, String pin, String password) {
}
