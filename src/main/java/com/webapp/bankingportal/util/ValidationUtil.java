package com.webapp.bankingportal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.repository.UserRepository;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class ValidationUtil {

    public static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final UserRepository userRepository;

    public static boolean isValidEmail(String identifier) {
        try {
            new InternetAddress(identifier).validate();
            return true;
        } catch (AddressException e) {
            log.warn("Invalid email address: {}", identifier);
        }

        return false;
    }

    public static boolean isValidAccountNumber(String identifier) {
        // Account number validation logic (e.g., length check)
        return identifier != null && identifier.length() == 6;
    }

    public static boolean isValidCountryCode(String countryCode) {
        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
            return false;
        }

        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumber parsedNumber = null;

        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
        } catch (NumberParseException e) {
            throw new UserInvalidException("Invalid phone number: " + e.getMessage());
        }

        return phoneNumberUtil.isValidNumber(parsedNumber);
    }

    public static void validatePassword(String password) {
        if (password.length() < 8) {
            throw new UserInvalidException("Password must be at least 8 characters long");
        }

        if (password.length() >= 128) {
            throw new UserInvalidException("Password must be less than 128 characters long");
        }

        if (password.matches(".*\\s.*")) {
            throw new UserInvalidException("Password cannot contain any whitespace characters");
        }

        val message = new StringBuilder();
        message.append("Password must contain at least ");

        var needsComma = false;
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
            val lastCommaIndex = message.lastIndexOf(",");
            if (lastCommaIndex > -1) {
                message.replace(lastCommaIndex, lastCommaIndex + 1, " and");
            }
            throw new UserInvalidException(message.toString());
        }
    }

    public static void validateUserDetailsNotEmpty(User user) {
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
        }

        if (user.getCountryCode() == null || user.getCountryCode().isEmpty()) {
            throw new UserInvalidException("Country code cannot be empty");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new UserInvalidException("Phone number cannot be empty");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UserInvalidException("Password cannot be empty");
        }
    }

    public static void validateUserDetails(User user) {
        validateUserDetailsNotEmpty(user);

        if (!isValidEmail(user.getEmail())) {
            throw new UserInvalidException("Invalid email: " + user.getEmail());
        }

        if (!isValidCountryCode(user.getCountryCode())) {
            throw new UserInvalidException("Invalid country code: " + user.getCountryCode());
        }

        if (!isValidPhoneNumber(user.getPhoneNumber(), user.getCountryCode())) {
            throw new UserInvalidException(
                    "Invalid phone number: " + user.getPhoneNumber() + " for country code: " + user.getCountryCode());
        }

        validatePassword(user.getPassword());
    }

    public void validateNewUser(User user) {
        validateUserDetails(user);
        if (doesEmailExist(user.getEmail())) {
            throw new UserInvalidException("Email already exists");
        }
        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException("Phone number already exists");
        }
    }

    public boolean doesAccountExist(String accountNumber) {
        return userRepository.findByAccountAccountNumber(accountNumber).isPresent();
    }

    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean doesPhoneNumberExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
