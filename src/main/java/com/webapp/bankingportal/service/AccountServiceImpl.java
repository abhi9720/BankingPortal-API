package com.webapp.bankingportal.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
    private  AccountRepository accountRepository;

    public Account createAccount(User user) {
        String accountNumber = generateUniqueAccountNumber();
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(0.0);
        account.setUser(user);
        
        user.setAccount(account);

        return accountRepository.save(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            // Generate a 6-digit random number as the account number
            int randomAccountNumber = new Random().nextInt(900000) + 100000;
            accountNumber = String.valueOf(randomAccountNumber);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

        return accountNumber;
    }
}
