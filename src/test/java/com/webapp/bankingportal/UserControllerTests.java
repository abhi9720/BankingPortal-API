package com.webapp.bankingportal;

import java.util.HashMap;

import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jayway.jsonpath.JsonPath;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.service.TokenService;
import com.webapp.bankingportal.util.JsonUtil;

import jakarta.mail.internet.MimeMessage;

public class UserControllerTests extends BaseTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void test_register_user_with_valid_details() throws Exception {
        createAndRegisterUser();
    }

    @Test
    public void test_register_user_with_empty_name() throws Exception {
        User user = createUser();
        user.setName("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_name() throws Exception {
        User user = createUser();
        user.setName(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_email() throws Exception {
        User user = createUser();
        user.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_email() throws Exception {
        User user = createUser();
        user.setEmail(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_country_code() throws Exception {
        User user = createUser();
        user.setCountryCode("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Country code cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_country_code() throws Exception {
        User user = createUser();
        user.setCountryCode(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Country code cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_phone_number() throws Exception {
        User user = createUser();
        user.setPhoneNumber("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_phone_number() throws Exception {
        User user = createUser();
        user.setPhoneNumber(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_address() throws Exception {
        User user = createUser();
        user.setAddress("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_address() throws Exception {
        User user = createUser();
        user.setAddress(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_register_user_with_duplicate_email() throws Exception {
        User user1 = createAndRegisterUser();
        User user2 = createUser();
        user2.setEmail(user1.getEmail());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email already exists"));
    }

    @Test
    public void test_register_user_with_duplicate_phone_number() throws Exception {
        User user1 = createAndRegisterUser();
        User user2 = createUser();
        user2.setPhoneNumber(user1.getPhoneNumber());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number already exists"));
    }

    @Test
    public void test_register_user_with_invalid_email() throws Exception {
        User user = createUser();
        user.setEmail(faker.lorem().word());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(StringContains.containsString("Invalid email")));
    }

    @Test
    public void test_register_user_with_invalid_country_code() throws Exception {
        User user = createUser();
        user.setCountryCode(faker.lorem().word());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid country code: " + user.getCountryCode()));
    }

    @Test
    public void test_register_user_with_invalid_phone_number() throws Exception {
        User user = createUser();
        user.setPhoneNumber(faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        MockMvcResultMatchers.content().string(StringContains.containsString("Invalid phone number")));
    }

    @Test
    public void test_register_user_with_empty_password() throws Exception {
        User user = createUser();
        user.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_password() throws Exception {
        User user = createUser();
        user.setPassword(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_register_user_with_short_password() throws Exception {
        User user = createUser();
        user.setPassword(faker.internet().password(1, MIN_PASSWORD_LENGTH - 1, true, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must be at least 8 characters long"));
    }

    @Test
    public void test_register_user_with_long_password() throws Exception {
        User user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH + 1, MAX_PASSWORD_LENGTH * 2, true, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must be less than 128 characters long"));
    }

    @Test
    public void test_register_user_with_password_containing_whitespace() throws Exception {
        User user = createUser();
        user.setPassword(faker.lorem().sentence());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot contain any whitespace characters"));
    }

    @Test
    public void test_register_user_with_password_missing_uppercase_letters() throws Exception {
        User user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, false, true));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one uppercase letter"));
    }

    @Test
    public void test_register_user_with_password_missing_lowercase_letters() throws Exception {
        User user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, true, true)
                .toUpperCase());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string(StringContains.containsString("Password must contain at least one lowercase letter")));
    }

    @Test
    public void test_register_user_with_password_missing_digits() throws Exception {
        User user = createUser();
        user.setPassword("!" + faker.lorem().characters(MAX_PASSWORD_LENGTH - 1, true, false));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one digit"));
    }

    @Test
    public void test_register_user_with_password_missing_special_characters() throws Exception {
        User user = createUser();
        user.setPassword(faker.internet().password(MAX_PASSWORD_LENGTH - 1, MAX_PASSWORD_LENGTH, true, false));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one special character"));
    }

    @Test
    public void test_login_with_valid_credentials() throws Exception {
        createAndLoginUser();
    }

    @Test
    public void test_login_with_invalid_account_number() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(getRandomAccountNumber());
        loginRequest.setPassword(getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_login_with_invalid_password() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_login_with_missing_account_number() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber("");
        loginRequest.setPassword(getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_login_with_missing_password() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_generate_otp_with_valid_account_number() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber(accountNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("OTP sent successfully to: " + user.getEmail()));
    }

    @Test
    public void test_generate_otp_with_invalid_account_number() throws Exception {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber(getRandomAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("User not found for the given account number"));
    }

    @Test
    public void test_generate_otp_with_missing_account_number() throws Exception {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("User not found for the given account number"));
    }

    @Test
    public void test_verify_otp_with_valid_account_number_and_otp() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber(accountNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("OTP sent successfully to: " + user.getEmail()));

        MimeMessage[] receivedMessages = GreenMailJavaMailSender.getReceivedMessagesForDomain(user.getEmail());
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber(accountNumber);
        otpVerificationRequest.setOtp(getOtpFromEmail(receivedMessages[0]));

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(accountNumber);
        pinRequest.setPassword(user.getPassword());
        pinRequest.setPin(getRandomPin());

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
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber(accountNumber);
        otpVerificationRequest.setOtp(getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid OTP"));
    }

    @Test
    public void test_verify_otp_with_missing_account_number() throws Exception {
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setOtp(getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Missing account number"));
    }

    @Test
    public void test_verify_otp_with_empty_account_number() throws Exception {
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber("");
        otpVerificationRequest.setOtp(getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Missing account number"));
    }

    @Test
    public void test_verify_otp_with_missing_otp() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber(accountNumber);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Missing OTP"));
    }

    @Test
    public void test_verify_otp_with_empty_otp() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber(accountNumber);
        otpVerificationRequest.setOtp("");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Missing OTP"));
    }

    @Test
    public void test_update_user_with_valid_details() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
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
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
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
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_address() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
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
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_email() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
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
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_phone_number() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
        updatedUser.setPhoneNumber("");
        updatedUser.setPassword(userDetails.get("password"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_password() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        User updatedUser = createUser();
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
        User updatedUser = createUser();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_with_valid_token() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer " + userDetails.get("token")))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn().getResponse();

        String redirectedUrl = response.getRedirectedUrl();
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

        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        UserDetails userDetails = tokenService.loadUserByUsername(accountNumber);
        String token = tokenService.generateToken(userDetails);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_logout_without_login() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        UserDetails userDetails = tokenService.loadUserByUsername(accountNumber);
        String token = tokenService.generateToken(userDetails);

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
