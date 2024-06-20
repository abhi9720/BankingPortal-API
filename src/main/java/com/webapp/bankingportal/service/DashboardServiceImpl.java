package com.webapp.bankingportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.dto.AccountResponse;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.UserRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserResponse getUserDetails(String accountNumber) {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException(
                        "User not found for the provided account number."));

        return new UserResponse(user);
    }

    @Override
    public AccountResponse getAccountDetails(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        // Check if the account exists with the provided account number
        if (account == null) {
            throw new NotFoundException("Account not found for the provided account number.");
        }

        // Map the account entity to AccountResponse DTO
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setBranch(account.getBranch());
        accountResponse.setIfscCode(account.getIfscCode());

        return accountResponse;
    }
}
