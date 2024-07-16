package com.webapp.bankingportal;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.util.ApiMessages;
import com.webapp.bankingportal.util.JsonUtil;

import lombok.val;

public class AccountControllerTests extends BaseTest {

    @Test
    public void test_pin_check_without_pin() throws Exception {
        val userDetails = createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/pin/check")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_NOT_CREATED.getMessage()));
    }

    @Test
    public void test_pin_check_with_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/pin/check")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_CREATED.getMessage()));
    }

    @Test
    public void test_pin_check_without_authorization() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/pin/check"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_pin_create_with_valid_password() throws Exception {
        createAndLoginUserWithPin();
    }

    @Test
    public void test_pin_create_with_invalid_password() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), getRandomPin(), getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_create_with_missing_password() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), getRandomPin(), null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_pin_create_with_missing_pin() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), null, userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_pin_create_with_invalid_short_pin() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), faker.number().digits(3),
                userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_create_with_invalid_long_pin() throws Exception {
        val userDetails = createAndLoginUser();

        val pinRequest = new PinRequest(userDetails.get("accountNumber"), faker.number().digits(5),
                userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_create_unauthorized_access() throws Exception {
        val pinRequest = new PinRequest(getRandomAccountNumber(), getRandomPin(), getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_pin_update_with_valid_data() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"),
                getRandomPin(), userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_UPDATE_SUCCESS.getMessage()));
    }

    @Test
    public void test_pin_update_with_invalid_password() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"),
                getRandomPin(), getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_invalid_old_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), getRandomPin(), getRandomPin(),
                userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_invalid_new_short_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"),
                faker.number().digits(3), userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_invalid_new_long_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"),
                faker.number().digits(5), userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_FORMAT_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_missing_password() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"),
                getRandomPin(), null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_missing_old_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), null, getRandomPin(),
                userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_missing_new_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val pinUpdateRequest = new PinUpdateRequest(userDetails.get("accountNumber"), userDetails.get("pin"), null,
                userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_pin_update_with_unauthorized_access() throws Exception {
        val pinUpdateRequest = new PinUpdateRequest(getRandomAccountNumber(), getRandomPin(), getRandomPin(),
                getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_deposit_with_valid_data() throws Exception {
        createAndLoginUserWithInitialBalance(100.0);
    }

    @Test
    public void test_deposit_with_invalid_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), getRandomPin(), 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_deposit_with_negative_amount() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), userDetails.get("pin"), -100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage()));
    }

    @Test
    public void test_deposit_with_excessively_large_amount() throws Exception {
        val userDetails = createAndLoginUserWithPin();
        val amountRequest = new AmountRequest(userDetails.get("accountNumber"),
                userDetails.get("pin"), 1000000.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_EXCEED_100_000_ERROR.getMessage()));
    }

    @Test
    public void test_deposit_with_missing_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), null, 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_deposit_with_unauthorized_access() throws Exception {
        val amountRequest = new AmountRequest(getRandomAccountNumber(), getRandomPin(), 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_withdraw_with_valid_pin_and_amount() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), userDetails.get("pin"), amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.CASH_WITHDRAWAL_SUCCESS.getMessage()));
    }

    @Test
    public void test_withdraw_with_invalid_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), getRandomPin(), 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_withdraw_with_negative_amount() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), userDetails.get("pin"), -100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage()));
    }

    @Test
    public void test_withdraw_with_zero_amount() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), userDetails.get("pin"), 0.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage()));
    }

    @Test
    public void test_withdraw_with_insufficient_funds() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), userDetails.get("pin"), amount * 2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage()));
    }

    @Test
    public void test_withdraw_with_missing_pin() throws Exception {
        val userDetails = createAndLoginUserWithPin();

        val amountRequest = new AmountRequest(userDetails.get("accountNumber"), null, 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_withdraw_with_unauthorized_access() throws Exception {
        val amountRequest = new AmountRequest(getRandomAccountNumber(), getRandomPin(), 100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_fund_transfer_with_valid_data() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.CASH_TRANSFER_SUCCESS.getMessage()));
    }

    @Test
    public void test_fund_transfer_to_the_same_account() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                userDetails.get("accountNumber"), amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.CASH_TRANSFER_SAME_ACCOUNT_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_invalid_source_account_pin() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), amount, getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_invalid_target_account() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"), getRandomAccountNumber(),
                amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.ACCOUNT_NOT_FOUND.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_insufficient_funds() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), amount * 2, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.BALANCE_INSUFFICIENT_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_negative_amount() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), -amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_zero_amount() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), 0.0, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.AMOUNT_NEGATIVE_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_missing_source_account_pin() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), amount, null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PIN_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_fund_transfer_with_missing_target_account() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"), null,
                amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.ACCOUNT_NOT_FOUND.getMessage()));
    }

    @Test
    public void test_fund_transfer_unauthorized_access() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        val fundTransferRequest = new FundTransferRequest(userDetails.get("accountNumber"),
                createAndLoginUser().get("accountNumber"), amount, userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_transactions_with_authorized_access() throws Exception {
        val amount = 100.0;
        val userDetails = createAndLoginUserWithInitialBalance(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(CoreMatchers.containsString("\"amount\":" + amount
                                + ",\"transactionType\":\"CASH_DEPOSIT\"")));
    }

    @Test
    public void test_transactions_unauthorized_access() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
