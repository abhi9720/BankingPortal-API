package com.webapp.bankingportal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.service.OtpService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private User createAndRegisterUser() throws Exception {
        User user = TestUtil.createUser();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        return user;
    }

    @Test
    public void test_register_user_with_valid_details() throws Exception {
        createAndRegisterUser();
    }

    @Test
    public void test_register_user_with_empty_name() throws Exception {
        User user = TestUtil.createUser();
        user.setName("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_name() throws Exception {
        User user = TestUtil.createUser();
        user.setName(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_address() throws Exception {
        User user = TestUtil.createUser();
        user.setAddress("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_address() throws Exception {
        User user = TestUtil.createUser();
        user.setAddress(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_email() throws Exception {
        User user = TestUtil.createUser();
        user.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_email() throws Exception {
        User user = TestUtil.createUser();
        user.setEmail(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_register_user_with_empty_phone_number() throws Exception {
        User user = TestUtil.createUser();
        user.setPhoneNumber("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_phone_number() throws Exception {
        User user = TestUtil.createUser();
        user.setPhoneNumber(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }

    @Test
    public void test_register_user_with_duplicate_email() throws Exception {
        User user1 = createAndRegisterUser();
        User user2 = TestUtil.createUser();
        user2.setEmail(user1.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email already exists"));
    }

    @Test
    public void test_register_user_with_duplicate_phone_number() throws Exception {
        User user1 = createAndRegisterUser();
        User user2 = TestUtil.createUser();
        user2.setPhoneNumber(user1.getPhoneNumber());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number already exists"));
    }

    @Test
    public void test_register_user_with_invalid_email() throws Exception {
        User user = TestUtil.createUser();
        user.setEmail(TestUtil.faker.lorem().word());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid email: Missing final '@domain'"));
    }

    @Test
    public void test_register_user_with_invalid_phone_number() throws Exception {
        User user = TestUtil.createUser();
        user.setPhoneNumber(TestUtil.faker.number().digits(3));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid phone number"));
    }

    @Test
    public void test_register_user_with_empty_password() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_register_user_with_missing_password() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot be empty"));
    }

    @Test
    public void test_register_user_with_short_password() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.internet().password(
                1,
                TestUtil.MIN_PASSWORD_LENGTH - 1,
                true,
                true));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must be at least 8 characters long"));
    }

    @Test
    public void test_register_user_with_long_password() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.internet().password(
                TestUtil.MAX_PASSWORD_LENGTH + 1,
                TestUtil.MAX_PASSWORD_LENGTH * 2,
                true,
                true));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must be less than 128 characters long"));
    }

    @Test
    public void test_register_user_with_password_containing_whitespace() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.lorem().sentence());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password cannot contain any whitespace characters"));
    }

    @Test
    public void test_register_user_with_password_missing_uppercase_letters() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.internet().password(
                TestUtil.MAX_PASSWORD_LENGTH - 1,
                TestUtil.MAX_PASSWORD_LENGTH,
                false,
                true));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one uppercase letter"));
    }

    @Test
    public void test_register_user_with_password_missing_lowercase_letters() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.internet().password(
                TestUtil.MAX_PASSWORD_LENGTH - 1,
                TestUtil.MAX_PASSWORD_LENGTH,
                true,
                true)
                .toUpperCase());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one lowercase letter"));
    }

    @Test
    public void test_register_user_with_password_missing_digits() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword("!" + TestUtil.faker.lorem().characters(
                TestUtil.MAX_PASSWORD_LENGTH - 1,
                true,
                false));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one digit"));
    }

    @Test
    public void test_register_user_with_password_missing_special_characters() throws Exception {
        User user = TestUtil.createUser();
        user.setPassword(TestUtil.faker.internet().password(
                TestUtil.MAX_PASSWORD_LENGTH - 1,
                TestUtil.MAX_PASSWORD_LENGTH,
                true,
                false));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Password must contain at least one special character"));
    }

    @Test
    public void test_login_with_valid_credentials() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void test_login_with_invalid_account_number() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(TestUtil.getRandomAccountNumber());
        loginRequest.setPassword(TestUtil.getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
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
        loginRequest.setPassword(TestUtil.getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void test_login_with_missing_account_number() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber("");
        loginRequest.setPassword(TestUtil.getRandomPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
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

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("OTP sent successfully to: " + user.getEmail()));
    }

    @Test
    public void test_generate_otp_with_invalid_account_number() throws Exception {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber(TestUtil.getRandomAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("User not found for the given account number"));
    }

    @Test
    public void test_generate_otp_with_missing_account_number() throws Exception {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setAccountNumber("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/generate-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("User not found for the given account number"));
    }

    @Test
    public void test_verify_otp_with_valid_otp() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber(accountNumber);
        otpVerificationRequest.setOtp(otpService.generateOTP(accountNumber));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
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
        otpVerificationRequest.setOtp(TestUtil.getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid OTP"));
    }

    @Test
    public void test_verify_otp_with_missing_account_number() throws Exception {
        OtpVerificationRequest otpVerificationRequest = new OtpVerificationRequest();
        otpVerificationRequest.setAccountNumber("");
        otpVerificationRequest.setOtp(TestUtil.getRandomOtp());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerificationRequest)))
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
        otpVerificationRequest.setOtp("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpVerificationRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Missing OTP"));
    }

    @Test
    public void test_update_user_with_valid_details() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        User updatedUser = TestUtil.createUser();
        updatedUser.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(updatedUser)))
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
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        User updatedUser = TestUtil.createUser();
        updatedUser.setName("");
        updatedUser.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Name cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_address() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        User updatedUser = TestUtil.createUser();
        updatedUser.setAddress("");
        updatedUser.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Address cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_email() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        User updatedUser = TestUtil.createUser();
        updatedUser.setEmail("");
        updatedUser.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Email cannot be empty"));
    }

    @Test
    public void test_update_user_with_invalid_phone_number() throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository
                .findByEmail(user.getEmail())
                .get()
                .getAccount()
                .getAccountNumber();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        User updatedUser = TestUtil.createUser();
        updatedUser.setPhoneNumber("");
        updatedUser.setPassword(user.getPassword());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Phone number cannot be empty"));
    }
}
