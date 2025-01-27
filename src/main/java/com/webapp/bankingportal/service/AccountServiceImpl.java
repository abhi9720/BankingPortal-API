package com.webapp.bankingportal.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.Transaction;
import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.FundTransferException;
import com.webapp.bankingportal.exception.InsufficientBalanceException;
import com.webapp.bankingportal.exception.InvalidAmountException;
import com.webapp.bankingportal.exception.InvalidPinException;
import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TransactionRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;

    @Override
    public Account createAccount(User user) {
        val account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        return account.getPin() != null;
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        } while (accountRepository.findByAccountNumber(accountNumber) != null);

        return accountNumber;
    }

    private void validatePin(String accountNumber, String pin) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        if (account.getPin() == null) {
            throw new UnauthorizedException(ApiMessages.PIN_NOT_CREATED.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(pin, account.getPin())) {
            throw new UnauthorizedException(ApiMessages.PIN_INVALID_ERROR.getMessage());
        }
    }

    private void validatePassword(String accountNumber, String password) {
        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        if (password == null || password.isEmpty()) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
            throw new UnauthorizedException(ApiMessages.PASSWORD_INVALID_ERROR.getMessage());
        }
    }

    @Override
    public void createPin(String accountNumber, String password, String pin) {
        validatePassword(accountNumber, password);

        val account = accountRepository.findByAccountNumber(accountNumber);
        if (account.getPin() != null) {
            throw new UnauthorizedException(ApiMessages.PIN_ALREADY_EXISTS.getMessage());
        }

        if (pin == null || pin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!pin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);
    }

    @Override
    public void updatePin(String accountNumber, String oldPin, String password, String newPin) {
        log.info("Updating PIN for account: {}", accountNumber);

        validatePassword(accountNumber, password);
        validatePin(accountNumber, oldPin);

        val account = accountRepository.findByAccountNumber(accountNumber);

        if (newPin == null || newPin.isEmpty()) {
            throw new InvalidPinException(ApiMessages.PIN_EMPTY_ERROR.getMessage());
        }

        if (!newPin.matches("[0-9]{4}")) {
            throw new InvalidPinException(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage());
        }

        account.setPin(passwordEncoder.encode(newPin));
        accountRepository.save(account);
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage());
        }

        if (amount % 100 != 0) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_NOT_MULTIPLE_OF_100_ERROR.getMessage());
        }

        if (amount > 100000) {
            throw new InvalidAmountException(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage());
        }
    }

    @Transactional
    @Override
    public void cashDeposit(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        val newBalance = currentBalance + amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void cashWithdrawal(String accountNumber, String pin, double amount) {
        validatePin(accountNumber, pin);
        validateAmount(amount);

        val account = accountRepository.findByAccountNumber(accountNumber);
        val currentBalance = account.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newBalance = currentBalance - amount;
        account.setBalance(newBalance);
        accountRepository.save(account);

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(account);
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void fundTransfer(String sourceAccountNumber, String targetAccountNumber, String pin, double amount) {
        validatePin(sourceAccountNumber, pin);
        validateAmount(amount);

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new FundTransferException(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage());
        }

        val targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        if (targetAccount == null) {
            throw new NotFoundException(ApiMessages.ACCOUNT_NOT_FOUND.getMessage());
        }

        val sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        val sourceBalance = sourceAccount.getBalance();
        if (sourceBalance < amount) {
            throw new InsufficientBalanceException(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage());
        }

        val newSourceBalance = sourceBalance - amount;
        sourceAccount.setBalance(newSourceBalance);
        accountRepository.save(sourceAccount);

        val targetBalance = targetAccount.getBalance();
        val newTargetBalance = targetBalance + amount;
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(targetAccount);

        val transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

}
