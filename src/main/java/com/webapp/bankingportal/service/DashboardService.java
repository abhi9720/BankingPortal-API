package com.webapp.bankingportal.service;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;

public interface DashboardService {
    UserResponse getUserDetails(String accountNumber);
    AccountResponse getAccountDetails(String accountNumber);
}