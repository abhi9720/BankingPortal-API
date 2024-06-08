package com.webapp.bankingportal;

import com.github.javafaker.Faker;
import com.webapp.bankingportal.entity.User;

public class TestUtil {

    public static final Faker faker = new Faker();
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 127;

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

    public static String getRandomPhoneNumber() {
        return "+2010" + faker.number().digits(8);
    }

    public static String getRandomOtp() {
        return faker.number().digits(6);
    }

    public static String getRandomPin() {
        return faker.number().digits(4);
    }

    public static User createUser() {
        User user = new User();

        user.setName(faker.name().fullName());
        user.setPassword(getRandomPassword());
        user.setEmail(faker.internet().safeEmailAddress());
        user.setAddress(faker.address().fullAddress());
        user.setPhoneNumber(getRandomPhoneNumber());

        return user;
    }
}