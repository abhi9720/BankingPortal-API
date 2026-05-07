package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.cache.annotation.Cacheable;
import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.service.AccountService;
import com.webapp.bankingportal.service.TransactionService;
import com.webapp.bankingportal.util.ApiMessages;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * REST controller exposing account-level operations for the authenticated user.
 *
 * <p>All endpoints in this controller require a valid JWT in the Authorization header.
 * The account number is always derived from the authentication context via
 * {@link com.webapp.bankingportal.util.LoggedinUser#getAccountNumber()}; the client
 * does not need to supply it in the request body.</p>
 *
 * <p>State-changing endpoints (deposit, withdraw, fund-transfer, PIN create/update)
 * are guarded by an idempotency cache so that duplicate requests within the TTL window
 * are safely deduplicated.</p>
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    /**
     * Checks whether a transaction PIN has been set for the authenticated user's account.
     *
     * @return {@code 200 OK} with a message indicating whether the PIN exists
     */
    @GetMapping("/pin/check")
    public ResponseEntity<String> checkAccountPIN() {
        val isPINValid = accountService.isPinCreated(LoggedinUser.getAccountNumber());
        val response = isPINValid ? ApiMessages.PIN_CREATED.getMessage()
                : ApiMessages.PIN_NOT_CREATED.getMessage();

        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new transaction PIN for the authenticated user's account.
     *
     * @param pinRequest the request containing the desired PIN and the user's login password
     * @return {@code 200 OK} with a success message
     */
    @PostMapping("/pin/create")
    @Cacheable(value = "idempotency", key = "T(com.webapp.bankingportal.util.LoggedinUser).getAccountNumber() + ':' + '/api/account/pin/create' + ':' + (#pinRequest).hashCode()")
    public ResponseEntity<String> createPIN(@RequestBody PinRequest pinRequest) {
        accountService.createPin(
                LoggedinUser.getAccountNumber(),
                pinRequest.password(),
                pinRequest.pin());

        return ResponseEntity.ok(ApiMessages.PIN_CREATION_SUCCESS.getMessage());
    }

    /**
     * Updates the transaction PIN for the authenticated user's account.
     *
     * @param pinUpdateRequest the request containing the old PIN, the new PIN, and the user's login password
     * @return {@code 200 OK} with a success message
     */
    @PostMapping("/pin/update")
    @Cacheable(value = "idempotency", key = "T(com.webapp.bankingportal.util.LoggedinUser).getAccountNumber() + ':' + '/api/account/pin/update' + ':' + (#pinUpdateRequest).hashCode()")
    public ResponseEntity<String> updatePIN(@RequestBody PinUpdateRequest pinUpdateRequest) {
        accountService.updatePin(
                LoggedinUser.getAccountNumber(),
                pinUpdateRequest.oldPin(),
                pinUpdateRequest.password(),
                pinUpdateRequest.newPin());

        return ResponseEntity.ok(ApiMessages.PIN_UPDATE_SUCCESS.getMessage());
    }

    /**
     * Deposits cash into the authenticated user's account.
     *
     * @param amountRequest the request containing the PIN and the amount to deposit
     * @return {@code 200 OK} with a success message
     */
    @PostMapping("/deposit")
    @Cacheable(value = "idempotency", key = "T(com.webapp.bankingportal.util.LoggedinUser).getAccountNumber() + ':' + '/api/account/deposit' + ':' + (#amountRequest).hashCode()")
    public ResponseEntity<String> cashDeposit(@RequestBody AmountRequest amountRequest) {
        accountService.cashDeposit(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        return ResponseEntity.ok(ApiMessages.CASH_DEPOSIT_SUCCESS.getMessage());
    }

    /**
     * Withdraws cash from the authenticated user's account.
     *
     * @param amountRequest the request containing the PIN and the amount to withdraw
     * @return {@code 200 OK} with a success message
     */
    @PostMapping("/withdraw")
    @Cacheable(value = "idempotency", key = "T(com.webapp.bankingportal.util.LoggedinUser).getAccountNumber() + ':' + '/api/account/withdraw' + ':' + (#amountRequest).hashCode()")
    public ResponseEntity<String> cashWithdrawal(@RequestBody AmountRequest amountRequest) {
        accountService.cashWithdrawal(
                LoggedinUser.getAccountNumber(),
                amountRequest.pin(),
                amountRequest.amount());

        return ResponseEntity.ok(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage());
    }

    /**
     * Transfers funds from the authenticated user's account to another account.
     *
     * @param fundTransferRequest the request containing the target account number, PIN, and amount
     * @return {@code 200 OK} with a success message
     */
    @PostMapping("/fund-transfer")
    @Cacheable(value = "idempotency", key = "T(com.webapp.bankingportal.util.LoggedinUser).getAccountNumber() + ':' + '/api/account/fund-transfer' + ':' + (#fundTransferRequest).hashCode()")
    public ResponseEntity<String> fundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
        accountService.fundTransfer(
                LoggedinUser.getAccountNumber(),
                fundTransferRequest.targetAccountNumber(),
                fundTransferRequest.pin(),
                fundTransferRequest.amount());

        return ResponseEntity.ok(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage());
    }

    /**
     * Returns the full transaction history for the authenticated user's account,
     * sorted in reverse chronological order.
     *
     * @return {@code 200 OK} with a JSON array of {@link com.webapp.bankingportal.dto.TransactionDTO} objects
     */
    @GetMapping("/transactions")
    public ResponseEntity<String> getAllTransactionsByAccountNumber() {
        val transactions = transactionService
                .getAllTransactionsByAccountNumber(LoggedinUser.getAccountNumber());
        return ResponseEntity.ok(JsonUtil.toJson(transactions));
    }
    /**
     * Emails a plain-text bank statement to the authenticated user's registered email address.
     *
     * @return {@code 200 OK} with a JSON confirmation message
     */
    @GetMapping("/send-statement")
    public ResponseEntity<String> sendBankStatement() {
        String accountNumber = LoggedinUser.getAccountNumber(); // Get logged-in user account
        transactionService.sendBankStatementByEmail(accountNumber);
        return ResponseEntity.ok("{\"message\": \"Bank statement sent to your email.\"}");
    }

}
