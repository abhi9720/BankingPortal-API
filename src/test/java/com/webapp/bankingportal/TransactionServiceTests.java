package com.webapp.bankingportal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webapp.bankingportal.entity.TransactionType;
import com.webapp.bankingportal.service.TransactionService;

import lombok.val;

public class TransactionServiceTests extends BaseTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void test_get_all_transactions_for_account_with_no_transactions() {
        val accountDetails = createAccountWithPin(passwordEncoder, userRepository, accountService);
        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));
        Assertions.assertNotNull(transactions);
        Assertions.assertTrue(transactions.isEmpty());
    }

    @Test
    public void test_get_all_transactions_after_single_deposit() {
        val amount = 1000.0;
        val accountDetails = createAccountWithInitialBalance(amount);
        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));
        Assertions.assertNotNull(transactions);
        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(amount, transactions.get(0).getAmount(), 0.01);
        Assertions.assertEquals(TransactionType.CASH_DEPOSIT, transactions.get(0).getTransactionType());
    }

    @Test
    public void test_get_all_transactions_after_deposit_and_withdrawal() {
        val depositAmount = 1000.0;
        val withdrawalAmount = 500.0;
        val accountDetails = createAccountWithInitialBalance(depositAmount);

        accountService.cashWithdrawal(accountDetails.get("accountNumber"), accountDetails.get("pin"), withdrawalAmount);

        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));
        Assertions.assertNotNull(transactions);
        Assertions.assertEquals(2, transactions.size());
    }

    @Test
    public void test_get_all_transactions_after_fund_transfer_includes_source_and_target() {
        val sourceBalance = 1000.0;
        val sourceAccountDetails = createAccountWithInitialBalance(sourceBalance);
        val targetAccountDetails = createAccountWithPin(passwordEncoder, userRepository, accountService);

        val transferAmount = 500.0;
        accountService.fundTransfer(
                sourceAccountDetails.get("accountNumber"),
                targetAccountDetails.get("accountNumber"),
                sourceAccountDetails.get("pin"),
                transferAmount);

        val sourceTransactions = transactionService
                .getAllTransactionsByAccountNumber(sourceAccountDetails.get("accountNumber"));
        Assertions.assertTrue(sourceTransactions.size() >= 2,
                "Source account should have at least deposit + transfer transactions");

        val targetTransactions = transactionService
                .getAllTransactionsByAccountNumber(targetAccountDetails.get("accountNumber"));
        Assertions.assertFalse(targetTransactions.isEmpty(),
                "Target account should have the received transfer transaction");
    }

    @Test
    public void test_get_all_transactions_returns_correct_transaction_types() {
        val depositAmount = 1000.0;
        val withdrawalAmount = 500.0;
        val accountDetails = createAccountWithInitialBalance(depositAmount);
        accountService.cashWithdrawal(accountDetails.get("accountNumber"), accountDetails.get("pin"), withdrawalAmount);

        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));

        val hasDeposit = transactions.stream()
                .anyMatch(t -> t.getTransactionType() == TransactionType.CASH_DEPOSIT);
        val hasWithdrawal = transactions.stream()
                .anyMatch(t -> t.getTransactionType() == TransactionType.CASH_WITHDRAWAL);

        Assertions.assertTrue(hasDeposit, "Transaction list should contain a deposit");
        Assertions.assertTrue(hasWithdrawal, "Transaction list should contain a withdrawal");
    }

    @Test
    public void test_get_all_transactions_sorted_descending_by_date() {
        val depositAmount = 1000.0;
        val accountDetails = createAccountWithInitialBalance(depositAmount);
        accountService.cashWithdrawal(accountDetails.get("accountNumber"), accountDetails.get("pin"), 500.0);

        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));
        Assertions.assertTrue(transactions.size() >= 2);

        for (int i = 0; i < transactions.size() - 1; i++) {
            Assertions.assertTrue(
                    transactions.get(i).getTransactionDate()
                            .compareTo(transactions.get(i + 1).getTransactionDate()) >= 0,
                    "Transactions should be sorted newest-first");
        }
    }

    @Test
    public void test_transaction_source_account_number_is_set() {
        val amount = 1000.0;
        val accountDetails = createAccountWithInitialBalance(amount);
        val transactions = transactionService.getAllTransactionsByAccountNumber(accountDetails.get("accountNumber"));

        Assertions.assertFalse(transactions.isEmpty());
        Assertions.assertEquals(accountDetails.get("accountNumber"),
                transactions.get(0).getSourceAccountNumber());
    }

    @Test
    public void test_send_bank_statement_with_null_account_number() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendBankStatementByEmail(null));
    }

    @Test
    public void test_send_bank_statement_with_empty_account_number() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendBankStatementByEmail(""));
    }

    @Test
    public void test_send_bank_statement_with_blank_account_number() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendBankStatementByEmail("   "));
    }

    @Test
    public void test_send_bank_statement_with_nonexistent_account_returns_silently() {
        Assertions.assertDoesNotThrow(() ->
                transactionService.sendBankStatementByEmail(getRandomAccountNumber()));
    }
}
