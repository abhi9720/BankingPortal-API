package com.webapp.bankingportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.core.userdetails.User.withUsername;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with account number: " + accountNumber));

        return withUsername(accountNumber).password(user.getPassword()).build();
    }
}
