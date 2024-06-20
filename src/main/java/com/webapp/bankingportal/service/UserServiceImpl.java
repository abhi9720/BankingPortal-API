package com.webapp.bankingportal.service;

import java.util.Optional;

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

    public UserServiceImpl(UserRepository userRepository, AccountService accountService,
            PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

    private void validateUserDetails(User user) {
        if (user == null) {
            throw new UserInvalidException("User details cannot be empty");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserInvalidException("Name cannot be empty");
        }

        if (user.getCountry() == null || user.getCountry().isEmpty()) {
            throw new UserInvalidException("Country cannot be empty");
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

        validatePhoneNumber(user.getPhoneNumber(), user.getCountry());

        validatePassword(user.getPassword());
    }

    private void validatePhoneNumber(String phoneNumber, String countryCode) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new UserInvalidException("Phone number cannot be empty");
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String exceptionMessage = "Invalid phone number: " + phoneNumber;
        try {
            PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, countryCode);
            if (phoneNumberUtil.isValidNumber(parsedNumber)) {
                return;
            }
        } catch (NumberParseException e) {
            exceptionMessage = e.getMessage();
        }

        throw new UserInvalidException(exceptionMessage);
    }

    private void validatePassword(String password) {
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
}
