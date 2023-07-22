package com.webapp.bankingportal.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.security.JwtTokenUtil;
@Service
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, AccountService accountService,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.passwordEncoder =  passwordEncoder;
    }
    
    public User registerUser(User user) {
        
    	 String encodedPassword = passwordEncoder.encode(user.getPassword());
         user.setPassword(encodedPassword);

        // Save the user details
        User savedUser = userRepository.save(user);

        // Create an account for the user
        accountService.createAccount(savedUser);

        return savedUser;
    }


}
