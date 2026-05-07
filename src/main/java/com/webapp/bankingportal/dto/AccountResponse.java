package com.webapp.bankingportal.dto;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private double balance;
    private String accountType;
    private String branch;
    private String ifscCode;
    private String currencyCode;
    private String currencySymbol;

    public AccountResponse(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.accountType = account.getAccountType();
        this.branch = account.getBranch();
        this.ifscCode = account.getIfscCode();
        Currency currency = account.getCurrency();
        if (currency != null) {
            this.currencyCode = currency.getCode();
            this.currencySymbol = currency.getSymbol();
        }
    }

}
