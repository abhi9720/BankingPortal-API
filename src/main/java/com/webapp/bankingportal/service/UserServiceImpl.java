package com.webapp.bankingportal.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.mapper.UserMapper;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.LoggedinUser;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, AccountService accountService, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = saveUser(user);

        Account account = accountService.createAccount(savedUser);
        savedUser.setAccount(account);
        saveUser(savedUser);

        return savedUser;
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User source) {
        User target = userRepository.findByAccountAccountNumber(LoggedinUser.getAccountNumber())
                .orElseThrow(() -> new UserInvalidException("User not found"));
        userMapper.updateUser(source, target);
        return saveUser(target);
    }

    @Override
    public boolean doesAccountExist(String accountNumber) {
        return userRepository.findByAccountAccountNumber(accountNumber).isPresent();
    }

    @Override
    public Optional<User> getUserByAccountNumber(String account_no) {
        return userRepository.findByAccountAccountNumber(account_no);
    }
}
