package com.webapp.bankingportal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.util.ValidationUtil;

import lombok.val;

public class ValidationUtilTests extends BaseTest {

    @Autowired
    private ValidationUtil validationUtil;

    // ---- isValidEmail ----

    @Test
    public void test_is_valid_email_with_valid_email() {
        Assertions.assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        Assertions.assertTrue(ValidationUtil.isValidEmail("user.name+tag@sub.domain.org"));
    }

    @Test
    public void test_is_valid_email_with_missing_at_symbol() {
        Assertions.assertFalse(ValidationUtil.isValidEmail("userexample.com"));
    }

    @Test
    public void test_is_valid_email_with_missing_domain() {
        Assertions.assertFalse(ValidationUtil.isValidEmail("user@"));
    }

    @Test
    public void test_is_valid_email_with_empty_string() {
        Assertions.assertFalse(ValidationUtil.isValidEmail(""));
    }

    // ---- isValidAccountNumber ----

    @Test
    public void test_is_valid_account_number_with_exactly_6_chars() {
        Assertions.assertTrue(ValidationUtil.isValidAccountNumber("ABC123"));
        Assertions.assertTrue(ValidationUtil.isValidAccountNumber("000000"));
    }

    @Test
    public void test_is_valid_account_number_too_short() {
        Assertions.assertFalse(ValidationUtil.isValidAccountNumber("ABC12"));
        Assertions.assertFalse(ValidationUtil.isValidAccountNumber(""));
    }

    @Test
    public void test_is_valid_account_number_too_long() {
        Assertions.assertFalse(ValidationUtil.isValidAccountNumber("ABC1234"));
    }

    @Test
    public void test_is_valid_account_number_with_null() {
        Assertions.assertFalse(ValidationUtil.isValidAccountNumber(null));
    }

    // ---- isValidCountryCode ----

    @Test
    public void test_is_valid_country_code_with_us() {
        Assertions.assertTrue(ValidationUtil.isValidCountryCode("US"));
    }

    @Test
    public void test_is_valid_country_code_with_gb() {
        Assertions.assertTrue(ValidationUtil.isValidCountryCode("GB"));
    }

    @Test
    public void test_is_valid_country_code_with_nonsense_code() {
        Assertions.assertFalse(ValidationUtil.isValidCountryCode("ZZ"));
        Assertions.assertFalse(ValidationUtil.isValidCountryCode("NOTACODE"));
    }

    // ---- validatePassword ----

    @Test
    public void test_validate_password_with_valid_password() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validatePassword(getRandomPassword()));
    }

    @Test
    public void test_validate_password_too_short() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("Ab1!"));
    }

    @Test
    public void test_validate_password_too_long() {
        val longPassword = "Aa1!" + "x".repeat(128);
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword(longPassword));
    }

    @Test
    public void test_validate_password_with_whitespace() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("Valid 1@Pass"));
    }

    @Test
    public void test_validate_password_missing_uppercase() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("valid1@pass"));
    }

    @Test
    public void test_validate_password_missing_lowercase() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("VALID1@PASS"));
    }

    @Test
    public void test_validate_password_missing_digit() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("Valid@Password"));
    }

    @Test
    public void test_validate_password_missing_special_character() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validatePassword("Valid1Password"));
    }

    // ---- validateUserDetailsNotEmpty ----

    @Test
    public void test_validate_user_details_not_empty_with_null_user() {
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validateUserDetailsNotEmpty(null));
    }

    @Test
    public void test_validate_user_details_not_empty_with_missing_name() {
        val user = createUser();
        user.setName(null);
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validateUserDetailsNotEmpty(user));
    }

    @Test
    public void test_validate_user_details_not_empty_with_missing_email() {
        val user = createUser();
        user.setEmail(null);
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validateUserDetailsNotEmpty(user));
    }

    @Test
    public void test_validate_user_details_not_empty_with_missing_address() {
        val user = createUser();
        user.setAddress(null);
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validateUserDetailsNotEmpty(user));
    }

    @Test
    public void test_validate_user_details_not_empty_with_missing_password() {
        val user = createUser();
        user.setPassword(null);
        Assertions.assertThrows(UserInvalidException.class,
                () -> ValidationUtil.validateUserDetailsNotEmpty(user));
    }

    // ---- doesAccountExist / doesEmailExist / doesPhoneNumberExist ----

    @Test
    public void test_does_account_exist_with_existing_account() throws Exception {
        val userDetails = createAndLoginUser();
        Assertions.assertTrue(validationUtil.doesAccountExist(userDetails.get("accountNumber")));
    }

    @Test
    public void test_does_account_exist_with_non_existing_account() {
        Assertions.assertFalse(validationUtil.doesAccountExist(getRandomAccountNumber()));
    }

    @Test
    public void test_does_email_exist_with_existing_email() throws Exception {
        val userDetails = createAndLoginUser();
        Assertions.assertTrue(validationUtil.doesEmailExist(userDetails.get("email")));
    }

    @Test
    public void test_does_email_exist_with_non_existing_email() {
        Assertions.assertFalse(validationUtil.doesEmailExist(faker.internet().safeEmailAddress()));
    }

    @Test
    public void test_does_phone_number_exist_with_existing_number() throws Exception {
        val userDetails = createAndLoginUser();
        Assertions.assertTrue(validationUtil.doesPhoneNumberExist(userDetails.get("phoneNumber")));
    }

    @Test
    public void test_does_phone_number_exist_with_non_existing_number() {
        val region = getRandomCountryCode();
        Assertions.assertFalse(validationUtil.doesPhoneNumberExist(getRandomPhoneNumber(region)));
    }
}
