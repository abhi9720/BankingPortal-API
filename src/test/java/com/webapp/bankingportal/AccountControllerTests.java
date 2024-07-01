package com.webapp.bankingportal;

import java.util.HashMap;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.util.JsonUtil;

public class AccountControllerTests extends BaseTest {

    @Test
    public void test_pin_check_without_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/pin/check")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasPIN")
                        .value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN Not Created"));
    }

    @Test
    public void test_pin_check_with_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/account/pin/check")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasPIN")
                        .value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN Created"));
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
        HashMap<String, String> userDetails = createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(getRandomPassword());
        pinRequest.setPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid password"));
    }

    @Test
    public void test_pin_create_with_missing_password() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_pin_create_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_pin_create_with_invalid_short_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));
        pinRequest.setPin(faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN must be 4 digits"));
    }

    @Test
    public void test_pin_create_with_invalid_long_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));
        pinRequest.setPin(faker.number().digits(5));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN must be 4 digits"));
    }

    @Test
    public void test_pin_create_unauthorized_access() throws Exception {
        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(getRandomAccountNumber());
        pinRequest.setPassword(getRandomPassword());
        pinRequest.setPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_pin_update_with_valid_data() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN updated successfully"));
    }

    @Test
    public void test_pin_update_with_invalid_password() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(getRandomPassword());
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid password"));
    }

    @Test
    public void test_pin_update_with_invalid_old_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(getRandomPin());
        pinUpdateRequest.setNewPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_pin_update_with_invalid_new_short_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN must be 4 digits"));
    }

    @Test
    public void test_pin_update_with_invalid_new_long_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(faker.number().digits(5));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN must be 4 digits"));
    }

    @Test
    public void test_pin_update_with_missing_password() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_pin_update_with_missing_old_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setNewPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_pin_update_with_missing_new_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN cannot be empty"));
    }

    @Test
    public void test_pin_update_with_unauthorized_access() throws Exception {
        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(getRandomAccountNumber());
        pinUpdateRequest.setPassword(getRandomPassword());
        pinUpdateRequest.setOldPin(getRandomPin());
        pinUpdateRequest.setNewPin(getRandomPin());

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
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(getRandomPin());
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_deposit_with_negative_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(-100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_zero_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(0.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_excessively_large_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(1000000.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount cannot be greater than 100,000"));
    }

    @Test
    public void test_deposit_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_deposit_with_missing_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_unauthorized_access() throws Exception {
        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(getRandomAccountNumber());
        amountRequest.setPin(getRandomPin());
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_withdraw_with_valid_pin_and_amount() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("Cash withdrawn successfully"));
    }

    @Test
    public void test_withdraw_with_invalid_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(getRandomPin());
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_withdraw_with_negative_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(-100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_zero_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(0.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_insufficient_funds() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount * 2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Insufficient balance"));
    }

    @Test
    public void test_withdraw_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_withdraw_with_missing_amount() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_unauthorized_access() throws Exception {
        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(getRandomAccountNumber());
        amountRequest.setPin(getRandomPin());
        amountRequest.setAmount(100.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_fund_transfer_with_valid_data() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("Fund transferred successfully"));
    }

    @Test
    public void test_fund_transfer_to_the_same_account() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Source and target account cannot be the same"));
    }

    @Test
    public void test_fund_transfer_with_invalid_source_account_pin() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(getRandomPin());
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_fund_transfer_with_invalid_target_account() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(getRandomAccountNumber());
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Target account not found"));
    }

    @Test
    public void test_fund_transfer_with_insufficient_funds() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount * 2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Insufficient balance"));
    }

    @Test
    public void test_fund_transfer_with_negative_amount() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(-amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_with_zero_amount() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(0.0);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_with_missing_source_account_pin() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_fund_transfer_with_missing_target_account() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Target account not found"));
    }

    @Test
    public void test_fund_transfer_with_missing_amount() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_unauthorized_access() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest
                .setTargetAccountNumber(createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_transactions_with_authorized_access() throws Exception {
        double amount = 100.0;
        HashMap<String, String> userDetails = createAndLoginUserWithInitialBalance(amount);

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
