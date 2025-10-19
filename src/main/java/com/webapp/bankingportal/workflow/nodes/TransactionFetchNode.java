package com.webapp.bankingportal.workflow.nodes;

import com.webapp.bankingportal.dto.TransactionDTO;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.service.TransactionService;
import com.webapp.bankingportal.util.LoggedinUser;

import org.bsc.langgraph4j.action.NodeAction;

import java.util.*;
import java.util.stream.Collectors;
import com.webapp.bankingportal.workflow.state.ReconState;

// --- Node 1: Transaction Fetch ---
public class TransactionFetchNode implements NodeAction<ReconState> {
    private final TransactionService transactionService;
    private final DashboardService dashboardService;

    public TransactionFetchNode(TransactionService transactionService, DashboardService dashboardService) {
        this.transactionService = transactionService;
        this.dashboardService = dashboardService;
    }

    @Override
    public Map<String, Object> apply(ReconState state) {
        String accountNumber = LoggedinUser.getAccountNumber();
        UserResponse userResponse = dashboardService.getUserDetails(accountNumber);
        List<TransactionDTO> transactions = transactionService
                .getAllTransactionsByAccountNumber(accountNumber);

        // Convert DTOs into safe string representations
        List<String> txnSummaries = transactions.stream()
                .map(txn -> String.format(
                        "ID:%d | Amount:%.2f | Type:%s | Date:%s | From:%s | To:%s",
                        txn.getId(),
                        txn.getAmount(),
                        txn.getTransactionType(),
                        txn.getTransactionDate(),
                        txn.getSourceAccountNumber(),
                        txn.getTargetAccountNumber()
                ))
                .collect(Collectors.toList());

        System.out.println("Fetched transactions for account: " + accountNumber);

        return Map.of(
                ReconState.ACCOUNT_KEY, accountNumber,
                ReconState.TXNS_KEY, txnSummaries,
                ReconState.USER_KEY, Map.of(
                        "name", userResponse.getName(),
                        "email", userResponse.getEmail()
                )
        );
    }
}
