package com.webapp.bankingportal;

import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jayway.jsonpath.JsonPath;
import com.webapp.bankingportal.dto.AmountRequest;
import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.service.AccountService;

public interface TestUtil {

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 127;

    public static final Faker faker = new Faker();
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    public static final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static String getRandomAccountNumber() {
        return faker.lorem().characters(
                6,
                false,
                true);
    }

    public static String getRandomPassword() {
        return "!" + faker.internet().password(
                MAX_PASSWORD_LENGTH - 2,
                MAX_PASSWORD_LENGTH - 1,
                true,
                true);
    }

    public static String getRandomCountryCode() {
        Object[] supportedRegions = phoneNumberUtil.getSupportedRegions().toArray();
        int index = faker.number().numberBetween(0, supportedRegions.length - 1);

        return supportedRegions[index].toString();
    }

    public static String getRandomPhoneNumber(String region) {
        PhoneNumber exampleNumber = phoneNumberUtil.getExampleNumber(region);

        for (int i = 0; i < 100; ++i) {
            String nationalNumber = String.valueOf(exampleNumber.getNationalNumber());
            String randomPhoneNumber = faker.number().digits(nationalNumber.length());

            try {
                PhoneNumber phoneNumber = phoneNumberUtil.parse(
                        randomPhoneNumber, region);

                if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                    return phoneNumberUtil.format(phoneNumber,
                            PhoneNumberUtil.PhoneNumberFormat.E164);
                }
            } catch (NumberParseException e) {
                // Continue to next attempt if parsing fails
            }
        }

        return phoneNumberUtil.format(exampleNumber,
                PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public static String getRandomOtp() {
        return faker.number().digits(6);
    }

    public static String getRandomPin() {
        return faker.number().digits(4);
    }

    public static User createUser() {
        String countryCode = getRandomCountryCode();
        String phoneNumber = getRandomPhoneNumber(countryCode);
        User user = new User();

        user.setName(faker.name().fullName());
        user.setPassword(getRandomPassword());
        user.setEmail(faker.internet().safeEmailAddress());
        user.setAddress(faker.address().fullAddress());
        user.setCountryCode(countryCode);
        user.setPhoneNumber(phoneNumber);

        return user;
    }

    public static User createAndRegisterUser(MockMvc mockMvc) throws Exception {
        User user = createUser();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        return user;
    }

    public static HashMap<String, String> createAndLoginUser(
            MockMvc mockMvc,
            UserRepository userRepository) throws Exception {

        User user = createAndRegisterUser(mockMvc);
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

        HashMap<String, String> userDetails = new HashMap<>();
        userDetails.put("name", user.getName());
        userDetails.put("email", user.getEmail());
        userDetails.put("countryCode", user.getCountryCode());
        userDetails.put("phoneNumber", user.getPhoneNumber());
        userDetails.put("address", user.getAddress());
        userDetails.put("accountNumber", accountNumber);
        userDetails.put("password", user.getPassword());
        userDetails.put("token", token);

        return userDetails;
    }

    public static HashMap<String, String> createAndLoginUserWithPin(
            MockMvc mockMvc,
            UserRepository userRepository) throws Exception {

        HashMap<String, String> userDetails = createAndLoginUser(mockMvc, userRepository);
        String accountNumber = userDetails.get("accountNumber");
        String password = userDetails.get("password");

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(accountNumber);
        pinRequest.setPassword(password);
        pinRequest.setPin(TestUtil.getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg")
                        .value("PIN created successfully"));

        userDetails.put("pin", pinRequest.getPin());

        return userDetails;
    }

    public static HashMap<String, String> createAndLoginUserWithInitialBalance(
            MockMvc mockMvc,
            UserRepository userRepository,
            double amount) throws Exception {

        HashMap<String, String> userDetails = TestUtil
                .createAndLoginUserWithPin(mockMvc, userRepository);

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

    public static HashMap<String, String> createAccount(
        PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AccountService accountService) {

        HashMap<String, String> accountDetails = new HashMap<>();
        User user = createUser();
        accountDetails.put("password", user.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        Account account = accountService.createAccount(user);
        accountDetails.put("accountNumber", account.getAccountNumber());

        return accountDetails;
    }

    public static HashMap<String, String> createAccountWithPin(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AccountService accountService) {

        HashMap<String, String> accountDetails = createAccount(
                passwordEncoder,
                userRepository,
                accountService);

        accountDetails.put("pin", TestUtil.getRandomPin());

        accountService.createPin(
                accountDetails.get("accountNumber"),
                accountDetails.get("password"),
                accountDetails.get("pin"));

        return accountDetails;
    }

    public static HashMap<String, String> createAccountWithInitialBalance(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AccountService accountService,
            double amount) {

        HashMap<String, String> accountDetails = createAccountWithPin(
                passwordEncoder,
                userRepository,
                accountService);

        accountService.cashDeposit(
                accountDetails.get("accountNumber"),
                accountDetails.get("pin"),
                amount);

        return accountDetails;
    }
}
