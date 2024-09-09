package com.webapp.bankingportal;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jayway.jsonpath.JsonPath;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.service.TokenService;
import com.webapp.bankingportal.util.ApiMessages;
import com.webapp.bankingportal.util.JsonUtil;

import lombok.val;

public class UserControllerTests extends BaseTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void test_register_user_with_valid_details() throws Exception {
        createAndRegisterUser();
    }

    @Test
    public void test_register_user_with_empty_name() throws Exception {
        val user = createUser();
        user.setName("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_NAME_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_name() throws Exception {
        val user = createUser();
        user.setName(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_NAME_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_empty_email() throws Exception {
        val user = createUser();
        user.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_EMAIL_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_email() throws Exception {
        val user = createUser();
        user.setEmail(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_EMAIL_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_empty_country_code() throws Exception {
        val user = createUser();
        user.setCountryCode("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_COUNTRY_CODE_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_country_code() throws Exception {
        val user = createUser();
        user.setCountryCode(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_COUNTRY_CODE_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_empty_phone_number() throws Exception {
        val user = createUser();
        user.setPhoneNumber("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_PHONE_NUMBER_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_phone_number() throws Exception {
        val user = createUser();
        user.setPhoneNumber(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_PHONE_NUMBER_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_empty_address() throws Exception {
        val user = createUser();
        user.setAddress("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_ADDRESS_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_address() throws Exception {
        val user = createUser();
        user.setAddress(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_ADDRESS_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_duplicate_email() throws Exception {
        val user1 = createAndRegisterUser();
        val user2 = createUser();
        user2.setEmail(user1.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_EMAIL_ALREADY_EXISTS_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_duplicate_phone_number() throws Exception {
        val user1 = createAndRegisterUser();
        val user2 = createUser();
        user2.setPhoneNumber(user1.getPhoneNumber());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_PHONE_NUMBER_ALREADY_EXISTS_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_invalid_email() throws Exception {
        val user = createUser();
        val email = faker.lorem().word();
        user.setEmail(email);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.USER_EMAIL_ADDRESS_INVALID_ERROR.getMessage(), email)));
    }

    @Test
    public void test_register_user_with_invalid_country_code() throws Exception {
        val user = createUser();
        val countryCode = faker.lorem().word();
        user.setCountryCode(countryCode);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.USER_COUNTRY_CODE_INVALID_ERROR.getMessage(), countryCode)));
    }

    @Test
    public void test_register_user_with_invalid_phone_number() throws Exception {
        val user = createUser();
        val phoneNumber = faker.number().digits(3);
        user.setPhoneNumber(phoneNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(),
                                phoneNumber, user.getCountryCode())));
    }

    @Test
    public void test_register_user_with_empty_password() throws Exception {
        val user = createUser();
        user.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_missing_password() throws Exception {
        val user = createUser();
        user.setPassword(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage()));
    }
    @Test
    public void test_register_user_with_short_password() throws Exception {
        val user = createUser();
        user.setPassword(faker.internet().password(1, MIN_PASSWORD_LENGTH - 1, true, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_TOO_SHORT_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_long_password() throws Exception {
        val user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH + 1, MAX_PASSWORD_LENGTH * 2, true, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_TOO_LONG_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_password_containing_whitespace() throws Exception {
        val user = createUser();
        user.setPassword(faker.lorem().sentence());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.PASSWORD_CONTAINS_WHITESPACE_ERROR.getMessage()));
    }

    @Test
    public void test_register_user_with_password_missing_uppercase_letters() throws Exception {
        val user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, false, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.PASSWORD_REQUIREMENTS_ERROR.getMessage(), "one uppercase letter")));
    }

    @Test
    public void test_register_user_with_password_missing_lowercase_letters() throws Exception {
        val user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, true, true)
                .toUpperCase());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(StringContains.containsString(String.format(ApiMessages.PASSWORD_REQUIREMENTS_ERROR.getMessage(), "one lowercase letter"))));
    }

    @Test
    public void test_register_user_with_password_missing_digits() throws Exception {
        val user = createUser();
        user.setPassword("!" + faker.lorem().characters(MAX_PASSWORD_LENGTH - 1, true, false));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.PASSWORD_REQUIREMENTS_ERROR.getMessage(), "one digit")));
    }

    @Test
    public void test_register_user_with_password_missing_special_characters() throws Exception {
        val user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, true, false));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.PASSWORD_REQUIREMENTS_ERROR.getMessage(), "one special character")));
    }

    @Test
    public void test_login_with_valid_credentials() throws Exception {
        createAndLoginUser();
    }

    @Test
    public void test_login_with_invalid_account_number() throws Exception {
        val loginRequest = new LoginRequest(getRandomAccountNumber(), getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void test_login_with_invalid_password() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val loginRequest = new LoginRequest(accountNumber, getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_login_with_missing_account_number() throws Exception {
        val loginRequest = new LoginRequest("", getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void test_login_with_missing_password() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val loginRequest = new LoginRequest(accountNumber, "");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_generate_otp_with_valid_account_number() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val otpRequest = new OtpRequest(accountNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail())));
    }

    @Test
    public void test_generate_otp_with_invalid_account_number() throws Exception {
        val otpRequest = new OtpRequest(getRandomAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void test_generate_otp_with_missing_account_number() throws Exception {
        val otpRequest = new OtpRequest("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void test_verify_otp_with_valid_account_number_and_otp() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val otpRequest = new OtpRequest(accountNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail())));

        val receivedMessages = GreenMailJavaMailSender.getReceivedMessagesForDomain(user.getEmail());
        val otpVerificationRequest = new OtpVerificationRequest(accountNumber, getOtpFromEmail(receivedMessages[0]));

        val loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        val responseBody = loginResult.getResponse().getContentAsString();
        val token = JsonPath.read(responseBody, "$.token");

        val pinRequest = new PinRequest(accountNumber, getRandomPin(), user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN created successfully"));
    }

    @Test
    public void test_verify_otp_with_invalid_otp() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val otpVerificationRequest = new OtpVerificationRequest(accountNumber, getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.OTP_INVALID_ERROR.getMessage()));
    }

    @Test
    public void test_verify_otp_with_missing_account_number() throws Exception {
        val otpVerificationRequest = new OtpVerificationRequest(null, getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage()));
    }

    @Test
    public void test_verify_otp_with_empty_account_number() throws Exception {
        val otpVerificationRequest = new OtpVerificationRequest("", getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage()));
    }

    @Test
    public void test_verify_otp_with_missing_otp() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val otpVerificationRequest = new OtpVerificationRequest(accountNumber, null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.OTP_MISSING_ERROR.getMessage()));
    }

    @Test
    public void test_verify_otp_with_empty_otp() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val otpVerificationRequest = new OtpVerificationRequest(accountNumber, "");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.OTP_MISSING_ERROR.getMessage()));
    }
    @Test
    public void test_update_user_with_valid_details() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setPassword(userDetails.get("password"));
        updatedUser.setPhoneNumber(getRandomPhoneNumber(userDetails.get("countryCode")));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value(updatedUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value(updatedUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address")
                        .value(updatedUser.getAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phoneNumber")
                        .value(updatedUser.getPhoneNumber()));
    }

    @Test
    public void test_update_user_with_invalid_name() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setName("");
        updatedUser.setPassword(userDetails.get("password"));
        updatedUser.setPhoneNumber(getRandomPhoneNumber(userDetails.get("countryCode")));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_NAME_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_update_user_with_invalid_address() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setAddress("");
        updatedUser.setPassword(userDetails.get("password"));
        updatedUser.setPhoneNumber(getRandomPhoneNumber(userDetails.get("countryCode")));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_ADDRESS_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_update_user_with_invalid_email() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setEmail("");
        updatedUser.setPassword(userDetails.get("password"));
        updatedUser.setPhoneNumber(getRandomPhoneNumber(userDetails.get("countryCode")));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_EMAIL_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_update_user_with_invalid_phone_number() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setPhoneNumber("");
        updatedUser.setPassword(userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(ApiMessages.USER_PHONE_NUMBER_EMPTY_ERROR.getMessage()));
    }

    @Test
    public void test_update_user_with_invalid_password() throws Exception {
        val userDetails = createAndLoginUser();

        val updatedUser = createUser();
        updatedUser.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Bad credentials"));
    }

    @Test
    public void test_update_user_without_authentication() throws Exception {
        val updatedUser = createUser();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_with_valid_token() throws Exception {
        val userDetails = createAndLoginUser();

        val response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        val redirectedUrl = response.getRedirectedUrl();
        if (redirectedUrl != null) {
            Assertions.assertEquals("/logout", redirectedUrl);
            mockMvc.perform(MockMvcRequestBuilders
                    .get(redirectedUrl))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        } else {
            Assertions.fail("Redirected URL is null");
        }
    }

    @Test
    public void test_logout_with_invalid_token() throws Exception {
        createAndLoginUser();

        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val userDetails = tokenService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_without_login() throws Exception {
        val user = createAndRegisterUser();
        val accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        val userDetails = tokenService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_with_malformed_token() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_without_authorization() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
