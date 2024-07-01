package com.webapp.bankingportal;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

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
import com.webapp.bankingportal.service.TokenService;
import com.webapp.bankingportal.util.JsonUtil;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@AutoConfigureMockMvc
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AccountService accountService;

    @Autowired
    TokenService tokenService;

    protected static final int MIN_PASSWORD_LENGTH = 8;
    protected static final int MAX_PASSWORD_LENGTH = 127;

    protected static final Faker faker = new Faker();
    protected static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    protected static String getRandomAccountNumber() {
        return faker.lorem().characters(6, false, true);
    }

    protected static String getRandomPassword() {
        return "!" + faker.internet().password(MAX_PASSWORD_LENGTH - 2, MAX_PASSWORD_LENGTH - 1, true, true);
    }

    protected static String getRandomCountryCode() {
        Object[] supportedRegions = phoneNumberUtil.getSupportedRegions().toArray();
        int index = faker.number().numberBetween(0, supportedRegions.length - 1);
        return supportedRegions[index].toString();
    }

    protected static String getRandomPhoneNumber(String region) {
        PhoneNumber exampleNumber = phoneNumberUtil.getExampleNumber(region);

        for (int i = 0; i < 100; ++i) {
            String nationalNumber = String.valueOf(exampleNumber.getNationalNumber());
            String randomPhoneNumber = faker.number().digits(nationalNumber.length());

            try {
                PhoneNumber phoneNumber = phoneNumberUtil.parse(randomPhoneNumber, region);
                if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                    return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                }
            } catch (NumberParseException e) {
                // Continue to next attempt if parsing fails
            }
        }
        return phoneNumberUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    protected static String getRandomOtp() {
        return faker.number().digits(6);
    }

    protected static String getRandomPin() {
        return faker.number().digits(4);
    }

    protected String generateToken(String username, String password) {
        return tokenService.generateToken(
                withUsername(username).password(password).build());
    }

    protected String generateToken(String username, String password, Date expiry) {
        return tokenService.generateToken(
                withUsername(username).password(password).build(), expiry);
    }

    protected static User createUser() {
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

    protected User createAndRegisterUser() throws Exception {
        User user = createUser();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        return user;
    }

    protected HashMap<String, String> createAndLoginUser()
            throws Exception {
        User user = createAndRegisterUser();
        String accountNumber = userRepository.findByEmail(user.getEmail()).get().getAccount().getAccountNumber();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccountNumber(accountNumber);
        loginRequest.setPassword(user.getPassword());

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(loginRequest)))
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

    protected HashMap<String, String> createAndLoginUserWithPin()
            throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();
        String accountNumber = userDetails.get("accountNumber");
        String password = userDetails.get("password");

        PinRequest pinRequest = new PinRequest();
        pinRequest.setAccountNumber(accountNumber);
        pinRequest.setPassword(password);
        pinRequest.setPin(getRandomPin());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/pin/create")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(pinRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("PIN created successfully"));

        userDetails.put("pin", pinRequest.getPin());
        return userDetails;
    }

    protected HashMap<String, String> createAndLoginUserWithInitialBalance(double amount) throws Exception {
        HashMap<String, String> userDetails = createAndLoginUserWithPin();
        AmountRequest amountRequest = new AmountRequest();
        amountRequest.setAccountNumber(userDetails.get("accountNumber"));
        amountRequest.setPin(userDetails.get("pin"));
        amountRequest.setAmount(amount);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account/deposit")
                .header("Authorization", "Bearer " + userDetails.get("token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(amountRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Cash deposited successfully"));

        return userDetails;
    }

    protected HashMap<String, String> createAccount() {
        HashMap<String, String> accountDetails = new HashMap<>();
        User user = createUser();
        accountDetails.put("password", user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        Account account = accountService.createAccount(user);
        accountDetails.put("accountNumber", account.getAccountNumber());
        return accountDetails;
    }

    protected HashMap<String, String> createAccountWithPin(PasswordEncoder passwordEncoder,
            UserRepository userRepository, AccountService accountService) {
        HashMap<String, String> accountDetails = createAccount();
        accountDetails.put("pin", getRandomPin());
        accountService.createPin(accountDetails.get("accountNumber"), accountDetails.get("password"),
                accountDetails.get("pin"));
        return accountDetails;
    }

    protected HashMap<String, String> createAccountWithInitialBalance(double amount) {
        HashMap<String, String> accountDetails = createAccountWithPin(passwordEncoder, userRepository, accountService);
        accountService.cashDeposit(accountDetails.get("accountNumber"), accountDetails.get("pin"), amount);
        return accountDetails;
    }

    protected static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
            throws MessagingException, IOException {

        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();

        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent());
                break;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
                if (result.length() > 0) {
                    break;
                }
            }
        }

        return result.toString();
    }

    protected static String getOtpFromEmail(MimeMessage message)
            throws IOException, MessagingException {

        String content = getTextFromMimeMultipart((MimeMultipart) message.getContent());
        Pattern pattern = Pattern.compile("<h2.*?>(\\d+)</h2>");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new RuntimeException("OTP not found in email");
    }
}
