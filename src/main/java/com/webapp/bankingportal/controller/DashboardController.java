package com.webapp.bankingportal.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.bankingportal.service.DashboardService;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * REST controller providing read-only summary views of the authenticated user
 * and their associated account.
 *
 * <p>All endpoints require a valid JWT. The account number is resolved from
 * the security context.</p>
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Returns profile and account details for the currently authenticated user.
     *
     * @return {@code 200 OK} with a JSON {@link com.webapp.bankingportal.dto.UserResponse UserResponse} body
     */
    @GetMapping("/user")
    public ResponseEntity<String> getUserDetails() {
        val accountNumber = LoggedinUser.getAccountNumber();
        val userResponse = dashboardService.getUserDetails(accountNumber);
        return ResponseEntity.ok(JsonUtil.toJson(userResponse));
    }

    /**
     * Returns account details (balance, type, branch, IFSC) for the authenticated user.
     *
     * @return {@code 200 OK} with a JSON {@link com.webapp.bankingportal.dto.AccountResponse AccountResponse} body
     */
    @GetMapping("/account")
    public ResponseEntity<String> getAccountDetails() {
        val accountNumber = LoggedinUser.getAccountNumber();
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(JsonUtil.toJson(accountResponse));
    }

}
