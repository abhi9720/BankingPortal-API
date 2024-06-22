package com.webapp.bankingportal.service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.webapp.bankingportal.entity.User;

public interface UserService {

    public User registerUser(User user);

    public User saveUser(User user);

    public User updateUser(User user);

    public boolean doesEmailExist(String email);

    public boolean doesPhoneNumberExist(String phoneNumber);

    public boolean doesAccountExist(String accountNumber);

    public Optional<User> getUserByAccountNumber(String accountNumber);

    public CompletableFuture<Boolean> sendLoginNotificationEmail(User user, String ip);
}
