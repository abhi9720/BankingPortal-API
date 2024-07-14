package com.webapp.bankingportal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webapp.bankingportal.exception.NotFoundException;
import com.webapp.bankingportal.service.DashboardService;

import lombok.val;

public class DashboardServiceTests extends BaseTest {

    @Autowired
    DashboardService dashboardService;

    @Test
    public void test_get_user_details_with_valid_account_number() throws Exception {
        val accountNumber = createAndLoginUser().get("accountNumber");
        val userResponse = dashboardService.getUserDetails(accountNumber);
        Assertions.assertNotNull(userResponse);
        Assertions.assertEquals(accountNumber, userResponse.getAccountNumber());
    }

    @Test
    public void test_get_user_details_with_invalid_account_number() throws Exception {
        val accountNumber = "123456789";
        Assertions.assertThrows(NotFoundException.class, () -> {
            dashboardService.getUserDetails(accountNumber);
        });
    }

    @Test
    public void test_get_account_details_with_valid_account_number() throws Exception {
        val accountNumber = createAndLoginUser().get("accountNumber");
        val accountResponse = dashboardService.getAccountDetails(accountNumber);
        Assertions.assertNotNull(accountResponse);
        Assertions.assertEquals(accountNumber, accountResponse.getAccountNumber());
    }

    @Test
    public void test_get_account_details_with_invalid_account_number() throws Exception {
        val accountNumber = "123456789";
        Assertions.assertThrows(NotFoundException.class, () -> {
            dashboardService.getAccountDetails(accountNumber);
        });
    }

}
