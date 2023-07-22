package com.webapp.bankingportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.UserResponse;

import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.util.LoginUser;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUserDetails() {
        String accountNumber = LoginUser.getAccountNumber();
        UserResponse userResponse = dashboardService.getUserDetails(accountNumber);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/account")
    public ResponseEntity<AccountResponse> getAccountDetails() {
        String accountNumber = LoginUser.getAccountNumber();
        AccountResponse accountResponse = dashboardService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(accountResponse);
    }
    
    
   
}