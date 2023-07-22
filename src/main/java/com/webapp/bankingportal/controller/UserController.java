package com.webapp.bankingportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.security.JwtTokenUtil;
import com.webapp.bankingportal.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public UserController(UserService userService,AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
                           UserDetailsService userDetailsService) {
    	this.userService =  userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        
        UserResponse userResponse = new UserResponse();
        userResponse.setName(registeredUser.getName());
        userResponse.setEmail(registeredUser.getEmail());
        userResponse.setAccountNumber(registeredUser.getAccount().getAccountNumber());
        userResponse.setIFSC_code(registeredUser.getAccount().getIFSC_code());
        userResponse.setBranch(registeredUser.getAccount().getBranch());
        userResponse.setAccount_type(registeredUser.getAccount().getAccount_type());
        

        return ResponseEntity.ok(userResponse);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user with the account number and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getAccountNumber(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Invalid credentials, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid account number or password");
        }

        // If authentication successful, generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getAccountNumber());
        System.out.println(userDetails);
        String token = jwtTokenUtil.generateToken(userDetails);

        // Return the JWT token in the response
        return ResponseEntity.ok(token);
    }


}
