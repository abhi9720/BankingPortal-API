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

/**
 * Utility class providing validation helpers for user registration and profile updates.
 *
 * <p>Static methods perform format validation (email, phone number, country code,
 * password complexity) and can be used without a Spring context. Instance methods
 * perform database-backed uniqueness checks and require dependency injection.</p>
 */
@Component
@RequiredArgsConstructor
public class ValidationUtil {

    public static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);
    public static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private final UserRepository userRepository;

    /**
     * Returns {@code true} if the given string is a syntactically valid email address.
     *
     * @param identifier the string to validate as an email address
     * @return {@code true} if valid, {@code false} otherwise
     */
    public static boolean isValidEmail(String identifier) {
        try {
            new InternetAddress(identifier).validate();
            return true;
        } catch (AddressException e) {
            log.warn("Invalid email address: {}", identifier);
        }

        return false;
    }

    /**
     * Returns {@code true} if the given string is a valid 6-character account number.
     *
     * @param identifier the string to validate as an account number
     * @return {@code true} if non-null and exactly 6 characters long
     */
    public static boolean isValidAccountNumber(String identifier) {
        return identifier != null && identifier.length() == 6;
    }

    /**
     * Returns {@code true} if the given country code is a recognized ISO 3166-1 alpha-2 region.
     *
     * @param countryCode the country code to validate (e.g., "US", "IN")
     * @return {@code true} if the code is supported by {@link PhoneNumberUtil}
     */
    public static boolean isValidCountryCode(String countryCode) {
        if (!phoneNumberUtil.getSupportedRegions().contains(countryCode)) {
            return false;
        }

        return true;
    }

    /**
     * Returns {@code true} if the given phone number is valid for the specified country.
     *
     * @param phoneNumber the national subscriber number to validate
     * @param countryCode the ISO 3166-1 alpha-2 country code used for parsing
     * @return {@code true} if the number is valid
     * @throws com.webapp.bankingportal.exception.UserInvalidException if the number cannot be parsed
     */
    public static boolean isValidPhoneNumber(String phoneNumber, String countryCode) {
        PhoneNumber parsedNumber = null;

        try {
            parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
        } catch (NumberParseException e) {
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), phoneNumber, countryCode));
        }

        return phoneNumberUtil.isValidNumber(parsedNumber);
    }

    /**
     * Validates that a plain-text password meets all complexity requirements:
     * 8–127 characters, no whitespace, at least one uppercase letter, one lowercase
     * letter, one digit, and one special character.
     *
     * @param password the plain-text password to validate
     * @throws com.webapp.bankingportal.exception.UserInvalidException if any requirement is not met
     */
    public static void validatePassword(String password) {
        if (password.length() < 8) {
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_SHORT_ERROR.getMessage());
        }

        if (password.length() >= 128) {
            throw new UserInvalidException(ApiMessages.PASSWORD_TOO_LONG_ERROR.getMessage());
        }

        if (password.matches(".*\\s.*")) {
            throw new UserInvalidException(ApiMessages.PASSWORD_CONTAINS_WHITESPACE_ERROR.getMessage());
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

    /**
     * Checks that all required {@link com.webapp.bankingportal.entity.User User} fields are present and non-empty.
     *
     * @param user the user object to check
     * @throws com.webapp.bankingportal.exception.UserInvalidException if the user is null or any required field is blank
     */
    public static void validateUserDetailsNotEmpty(User user) {
        if (user == null) {
            throw new UserInvalidException(ApiMessages.USER_DETAILS_EMPTY_ERROR.getMessage());
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_NAME_EMPTY_ERROR.getMessage());
        }

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_ADDRESS_EMPTY_ERROR.getMessage());
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_EMAIL_EMPTY_ERROR.getMessage());
        }

        if (user.getCountryCode() == null || user.getCountryCode().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_COUNTRY_CODE_EMPTY_ERROR.getMessage());
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_EMPTY_ERROR.getMessage());
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UserInvalidException(ApiMessages.PASSWORD_EMPTY_ERROR.getMessage());
        }
    }

    /**
     * Validates all user details: checks for non-empty fields and then validates
     * the email format, country code, phone number, and password complexity.
     *
     * @param user the user object to validate
     * @throws com.webapp.bankingportal.exception.UserInvalidException if any field is invalid
     */
    public static void validateUserDetails(User user) {
        validateUserDetailsNotEmpty(user);

        if (!isValidEmail(user.getEmail())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_EMAIL_ADDRESS_INVALID_ERROR.getMessage(), user.getEmail()));
        }

        if (!isValidCountryCode(user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_COUNTRY_CODE_INVALID_ERROR.getMessage(), user.getCountryCode()));
        }

        if (!isValidPhoneNumber(user.getPhoneNumber(), user.getCountryCode())) {
            throw new UserInvalidException(String.format(ApiMessages.USER_PHONE_NUMBER_INVALID_ERROR.getMessage(), user.getPhoneNumber(), user.getCountryCode()));
        }

        validatePassword(user.getPassword());
    }

    /**
     * Validates user details and additionally checks that the email and phone number
     * are not already registered in the database.
     *
     * @param user the new user to validate
     * @throws com.webapp.bankingportal.exception.UserInvalidException if any field is invalid or the email/phone already exists
     */
    public void validateNewUser(User user) {
        validateUserDetails(user);
        if (doesEmailExist(user.getEmail())) {
            throw new UserInvalidException(ApiMessages.USER_EMAIL_ALREADY_EXISTS_ERROR.getMessage());
        }
        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException(ApiMessages.USER_PHONE_NUMBER_ALREADY_EXISTS_ERROR.getMessage());
        }
    }

    /**
     * Returns {@code true} if an account with the given account number exists in the database.
     *
     * @param accountNumber the account number to look up
     * @return {@code true} if a matching account is found
     */
    public boolean doesAccountExist(String accountNumber) {
        return userRepository.findByAccountAccountNumber(accountNumber).isPresent();
    }

    /**
     * Returns {@code true} if a user with the given email address already exists in the database.
     *
     * @param email the email address to check
     * @return {@code true} if the email is already registered
     */
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Returns {@code true} if a user with the given phone number already exists in the database.
     *
     * @param phoneNumber the phone number to check
     * @return {@code true} if the phone number is already registered
     */
    public boolean doesPhoneNumberExist(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

}
