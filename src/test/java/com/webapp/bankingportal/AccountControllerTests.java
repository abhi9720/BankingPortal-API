package com.webapp.bankingportal;

import java.util.HashMap;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.FundTransferRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.dto.PinUpdateRequest;
import com.webapp.bankingportal.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private TestUtil testUtil;

    @BeforeEach
    public void setup() {
        testUtil = new TestUtil(mockMvc, userRepository);
    }

    private HashMap<String, String> createAndLoginUserWithAmount(double amount)
            throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("Cash deposited successfully"));

        return userDetails;
    }

    @Test
    public void test_pin_check_without_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

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
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

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
        testUtil.createAndLoginUserWithPin();
    }

    @Test
    public void test_pin_create_with_invalid_password() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(TestUtil.getRandomPassword());
        pinRequest.setPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid password"));
    }

    @Test
    public void test_pin_create_with_missing_password() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_pin_create_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_pin_create_with_invalid_short_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));
        pinRequest.setPin(TestUtil.faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN must be 4 digits"));
    }

    @Test
    public void test_pin_create_with_invalid_long_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUser();

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinRequest.setPassword(userDetails.get("password"));
        pinRequest.setPin(TestUtil.faker.number().digits(5));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN must be 4 digits"));
    }

    @Test
    public void test_pin_create_unauthorized_access() throws Exception {
        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(TestUtil.getRandomAccountNumber());
        pinRequest.setPassword(TestUtil.getRandomPassword());
        pinRequest.setPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_pin_update_with_valid_data() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN updated successfully"));
    }

    @Test
    public void test_pin_update_with_invalid_password() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(TestUtil.getRandomPassword());
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid password"));
    }

    @Test
    public void test_pin_update_with_invalid_old_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(TestUtil.getRandomPin());
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_pin_update_with_invalid_new_short_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(TestUtil.faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN must be 4 digits"));
    }

    @Test
    public void test_pin_update_with_invalid_new_long_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(TestUtil.faker.number().digits(5));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN must be 4 digits"));
    }

    @Test
    public void test_pin_update_with_missing_password() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_pin_update_with_missing_old_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_pin_update_with_missing_new_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(userDetails.get("accountNumber"));
        pinUpdateRequest.setPassword(userDetails.get("password"));
        pinUpdateRequest.setOldPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("New PIN cannot be empty"));
    }

    @Test
    public void test_pin_update_with_unauthorized_access() throws Exception {
        PinUpdateRequest pinUpdateRequest = new PinUpdateRequest();
        pinUpdateRequest.setAccountNumber(TestUtil.getRandomAccountNumber());
        pinUpdateRequest.setPassword(TestUtil.getRandomPassword());
        pinUpdateRequest.setOldPin(TestUtil.getRandomPin());
        pinUpdateRequest.setNewPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(pinUpdateRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_deposit_with_valid_data() throws Exception {
        createAndLoginUserWithAmount(100.00);
    }

    @Test
    public void test_deposit_with_invalid_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(TestUtil.getRandomPin());
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_deposit_with_negative_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(-100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_zero_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(0.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_excessively_large_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(1000000.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount cannot be greater than 100,000"));
    }

    @Test
    public void test_deposit_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_deposit_with_missing_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_deposit_with_unauthorized_access() throws Exception {
        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(TestUtil.getRandomAccountNumber());
        amountRequest.setPin(TestUtil.getRandomPin());
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_withdraw_with_valid_pin_and_amount() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("Cash withdrawn successfully"));
    }

    @Test
    public void test_withdraw_with_invalid_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(TestUtil.getRandomPin());
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_withdraw_with_negative_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(-100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_zero_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(0.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_insufficient_funds() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount * 2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Insufficient balance"));
    }

    @Test
    public void test_withdraw_with_missing_pin() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_withdraw_with_missing_amount() throws Exception {
        HashMap<String, String> userDetails = testUtil.createAndLoginUserWithPin();

        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_withdraw_with_unauthorized_access() throws Exception {
        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(TestUtil.getRandomAccountNumber());
        amountRequest.setPin(TestUtil.getRandomPin());
        amountRequest.setAmount(100.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_fund_transfer_with_valid_data() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("Fund transferred successfully"));
    }

    @Test
    public void test_fund_transfer_to_the_same_account() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Source and target account cannot be the same"));
    }

    @Test
    public void test_fund_transfer_with_invalid_source_account_pin() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(TestUtil.getRandomPin());
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid PIN"));
    }

    @Test
    public void test_fund_transfer_with_invalid_target_account() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(TestUtil.getRandomAccountNumber());
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Target account not found"));
    }

    @Test
    public void test_fund_transfer_with_insufficient_funds() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount * 2);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Insufficient balance"));
    }

    @Test
    public void test_fund_transfer_with_negative_amount() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(-amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_with_zero_amount() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(0.00);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_with_missing_source_account_pin() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("PIN cannot be empty"));
    }

    @Test
    public void test_fund_transfer_with_missing_target_account() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Target account not found"));
    }

    @Test
    public void test_fund_transfer_with_missing_amount() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Amount must be greater than 0"));
    }

    @Test
    public void test_fund_transfer_unauthorized_access() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

        FundTransferRequest fundTransferRequest = new FundTransferRequest();
        fundTransferRequest.setSourceAccountNumber(userDetails.get("accountNumber"));
        fundTransferRequest.setTargetAccountNumber(testUtil.createAndLoginUser().get("accountNumber"));
        fundTransferRequest.setPin(userDetails.get("pin"));
        fundTransferRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/fund-transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_transactions_with_authorized_access() throws Exception {
        double amount = 100.00;
        HashMap<String, String> userDetails = createAndLoginUserWithAmount(amount);

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
