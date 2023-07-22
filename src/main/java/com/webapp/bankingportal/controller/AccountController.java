package com.webapp.bankingportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.util.LoginUser;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;



    // Existing APIs ...

    @PostMapping("/pin/check")
    public ResponseEntity<String> checkAccountPIN(@RequestBody PinRequest pinRequest) {
        boolean isPINValid = accountService.checkAccountPIN(LoginUser.getAccountNumber(), pinRequest.getPin());
        if (isPINValid) {
            return ResponseEntity.ok("PIN is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PIN");
        }
    }

    @PostMapping("/pin/create")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
        accountService.createPIN(LoginUser.getAccountNumber(), pinRequest.getPassword(), pinRequest.getPin());
        return ResponseEntity.ok("PIN created successfully");
    }

    @PostMapping("/pin/update")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
        accountService.updatePIN(LoginUser.getAccountNumber(), pinUpdateRequest.getOldPin(), pinUpdateRequest.getPassword(), pinUpdateRequest.getNewPin());
        return ResponseEntity.ok("PIN updated successfully");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
        accountService.cashDeposit(LoginUser.getAccountNumber(), amountRequest.getPin(), amountRequest.getAmount());
        return ResponseEntity.ok("Cash deposited successfully");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
        accountService.cashWithdrawal(LoginUser.getAccountNumber(), amountRequest.getPin(), amountRequest.getAmount());
        return ResponseEntity.ok("Cash withdrawn successfully");
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
        accountService.fundTransfer(LoginUser.getAccountNumber(), fundTransferRequest.getTargetAccountNumber(), fundTransferRequest.getPin(), fundTransferRequest.getAmount());
        return ResponseEntity.ok("Fund transferred successfully");
    }
}
