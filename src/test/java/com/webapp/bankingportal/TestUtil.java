package com.webapp.bankingportal;

import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jayway.jsonpath.JsonPath;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.PinRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;

public class TestUtil {

    private MockMvc mockMvc;
    private UserRepository userRepository;

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 127;

    public static final Faker faker = new Faker();
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    public static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public TestUtil(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

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

        PhoneNumber phoneNumber = phoneNumberUtil.getExampleNumber(region);
        String randomPhoneNumber = null;
        String nationalNumber;
        String operatorPrefix;
        String randomDigits;

        for (int i = 0; i < 30; ++i) {
            nationalNumber = String.valueOf(phoneNumber.getNationalNumber());
            operatorPrefix = nationalNumber.substring(0, 3);
            randomDigits = faker.number().digits(nationalNumber.length() - 3);
            randomPhoneNumber = operatorPrefix + randomDigits;

            phoneNumber.setNationalNumber(Long.valueOf(randomPhoneNumber));

            if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                break;
            }
        }

        if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
            randomPhoneNumber = String.valueOf(phoneNumberUtil
                    .getExampleNumber(region)
                    .getNationalNumber());
        }

        return randomPhoneNumber;
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

    public User createAndRegisterUser() throws Exception {
        User user = createUser();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        return user;
    }

    public HashMap<String, String> createAndLoginUser() throws Exception {
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

    public HashMap<String, String> createAndLoginUserWithPin() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();
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
}
