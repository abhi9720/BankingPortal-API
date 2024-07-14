package com.webapp.bankingportal.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.PasswordResetException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.mapper.UserMapper;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.LoggedinUser;
import com.webapp.bankingportal.util.ValidationUtil;

import lombok.val;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final GeolocationService geolocationService;

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
        ValidationUtil.validateUserDetails(user);

        if (doesEmailExist(user.getEmail())) {
            throw new UserInvalidException("Email already exists");
        }

        if (doesPhoneNumberExist(user.getPhoneNumber())) {
            throw new UserInvalidException("Phone number already exists");
        }

        ValidationUtil.validateUserDetails(user);

        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        val savedUser = saveUser(user);

        val account = accountService.createAccount(savedUser);
        savedUser.setAccount(account);

        return saveUser(savedUser);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User source) {
        val accountNumber = LoggedinUser.getAccountNumber();
        val target = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UserInvalidException(
                        "User with account number " + accountNumber + " does not exist"));

        ValidationUtil.validateUserDetails(source);

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
    public boolean doesIdentifierExist(String identifier) {
        return doesAccountExist(identifier) || doesEmailExist(identifier);
    }

    @Override
    public Optional<User> getUserByAccountNumber(String accountNo) {
        return userRepository.findByAccountAccountNumber(accountNo);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByIdentifier(String identifier) {
        Optional<User> user = Optional.empty();

        if (doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        }

        return user;
    }

    @Override
    public CompletableFuture<Boolean> sendLoginNotificationEmail(User user, String ip) {
        val name = user.getName();
        val email = user.getEmail();
        val subject = "New login to OneStopBank";
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

        return geolocationService.getGeolocation(ip).thenComposeAsync(geolocationResponse -> {

            val loginLocation = String.format("%s, %s",
                    geolocationResponse.getCity().getNames().get("en"),
                    geolocationResponse.getCountry().getNames().get("en"));

            val emailText = emailService.getLoginEmailTemplate(
                    name, loginTime, loginLocation);

            return emailService.sendEmail(email, subject, emailText)
                    .thenApplyAsync(result -> true)
                    .exceptionally(ex -> false);

        }).exceptionallyComposeAsync(throwable -> {

            val emailText = emailService.getLoginEmailTemplate(
                    name, loginTime, "Unknown");

            return emailService.sendEmail(email, subject, emailText)
                    .thenApplyAsync(result -> true)
                    .exceptionally(ex -> false);
        });
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException("Failed to reset password", e);
        }
    }

}
