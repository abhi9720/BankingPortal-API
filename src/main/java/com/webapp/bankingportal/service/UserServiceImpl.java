package com.webapp.bankingportal.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.mapper.UserMapper;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.LoggedinUser;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final GeolocationService geolocationService;

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public UserServiceImpl(
            UserRepository userRepository,
            AccountService accountService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            EmailService emailService,
            GeolocationService geolocationService) {

        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.geolocationService = geolocationService;
    }

    @Override
    public User registerUser(User user) {
        if (doesEmailExist(user.getEmail())) {
            throw new UserInvalidException("Email already exists");
        }

        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException("Phone number already exists");
        }

        validateUserDetails(user);

        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = saveUser(user);

        Account account = accountService.createAccount(savedUser);
        savedUser.setAccount(account);

        return saveUser(savedUser);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User source) {
        String accountNumber = LoggedinUser.getAccountNumber();
        User target = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UserInvalidException(
                        "User with account number " + accountNumber + " does not exist"));

        validateUserDetails(source);

        source.setPassword(target.getPassword());
        userMapper.updateUser(source, target);

        return saveUser(target);
    }

    @Override
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean doesPhoneNumberExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public boolean doesAccountExist(String accountNumber) {
        return userRepository.findByAccountAccountNumber(accountNumber).isPresent();
    }

    @Override
    public Optional<User> getUserByAccountNumber(String accountNo) {
        return userRepository.findByAccountAccountNumber(accountNo);
    }

    private static void validateUserDetails(User user) {
        if (user == null) {
            throw new UserInvalidException("User details cannot be empty");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserInvalidException("Name cannot be empty");
        }

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new UserInvalidException("Address cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserInvalidException("Email cannot be empty");
        } else {
            try {
                new InternetAddress(user.getEmail()).validate();
            } catch (AddressException e) {
                throw new UserInvalidException("Invalid email: " + e.getMessage());
            }
        }

        validateCountryCode(user.getCountryCode());
        validatePhoneNumber(user.getPhoneNumber(), user.getCountryCode());
        validatePassword(user.getPassword());
    }

    private static void validateCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            throw new UserInvalidException("Country code cannot be empty");
        }

        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
            throw new UserInvalidException("Invalid country code: " + countryCode);
        }
    }

    private static void validatePhoneNumber(String phoneNumber, String countryCode) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new UserInvalidException("Phone number cannot be empty");
        }

        PhoneNumber parsedNumber = null;

        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
        } catch (NumberParseException e) {
            throw new UserInvalidException("Invalid phone number: " + e.getMessage());
        }

        if (!phoneNumberUtil.isValidNumber(parsedNumber)) {
            throw new UserInvalidException("Invalid phone number: " + parsedNumber);
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new UserInvalidException("Password cannot be empty");
        }

        if (password.length() < 8) {
            throw new UserInvalidException("Password must be at least 8 characters long");
        }

        if (password.length() >= 128) {
            throw new UserInvalidException("Password must be less than 128 characters long");
        }

        if (password.matches(".*\\s.*")) {
            throw new UserInvalidException("Password cannot contain any whitespace characters");
        }

        StringBuilder message = new StringBuilder();
        message.append("Password must contain at least ");

        boolean needsComma = false;
        if (!password.matches(".*[A-Z].*")) {
            message.append("one uppercase letter");
            needsComma = true;
        }

        if (!password.matches(".*[a-z].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one lowercase letter");
            needsComma = true;
        }

        if (!password.matches(".*[0-9].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one digit");
            needsComma = true;
        }

        if (!password.matches(".*[^A-Za-z0-9].*")) {
            if (needsComma) {
                message.append(", ");
            }
            message.append("one special character");
        }

        if (message.length() > "Password must contain at least ".length()) {
            int lastCommaIndex = message.lastIndexOf(",");
            if (lastCommaIndex > -1) {
                message.replace(lastCommaIndex, lastCommaIndex + 1, " and");
            }
            throw new UserInvalidException(message.toString());
        }
    }

    @Override
    public CompletableFuture<Boolean> sendLoginNotificationEmail(User user, String ip) {
        final String name = user.getName();
        final String email = user.getEmail();
        final String loginTime = new Timestamp(System.currentTimeMillis()).toString();

        return geolocationService.getGeolocation(ip).thenComposeAsync(geolocationResponse -> {

            final String loginLocation = String.format("%s, %s",
                    geolocationResponse.getCity().getNames().get("en"),
                    geolocationResponse.getCountry().getNames().get("en"));

            final String emailText = emailService.getLoginEmailTemplate(
                    name, loginTime, loginLocation);

            return emailService.sendEmail(email, "OneStopBank Login", emailText)
                    .thenApplyAsync(result -> true)
                    .exceptionally(ex -> false);

        }).exceptionallyComposeAsync(throwable -> {

            final String emailText = emailService.getLoginEmailTemplate(
                    name, loginTime, "Unknown");

            return emailService.sendEmail(email, "OneStopBank Login", emailText)
                    .thenApplyAsync(result -> true)
                    .exceptionally(ex -> false);
        });
    }
}
