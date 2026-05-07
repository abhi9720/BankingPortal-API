package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for user registration, authentication, and profile management.
 *
 * <p>Public endpoints (register, login, OTP, logout) do not require a JWT.
 * The update endpoint requires a valid JWT and uses the authenticated user's
 * password for additional verification.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
@Autowired
    private final UserService userService;
@Autowired
    private EmailService emailService;

    /**
     * Registers a new user, creates their bank account, and sends a welcome email.
     *
     * @param user the user details to register (validated by Bean Validation)
     * @return {@code 200 OK} with a {@link com.webapp.bankingportal.dto.UserResponse UserResponse} JSON body
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        ResponseEntity<String> response = userService.registerUser(user);

        // Only send email if registration succeeded
        if (response.getStatusCode().is2xxSuccessful()) {
            String emailBody = emailService.getBankStatementEmailTemplate(user.getName(), "Welcome! Your account is created.");
            emailService.sendEmail(user.getEmail(), "Welcome to OneStopBank", emailBody);
        }

        return response;
    }

    /**
     * Authenticates the user with a password and returns a JWT on success.
     *
     * @param loginRequest the login credentials (identifier + password)
     * @param request      the HTTP request used to extract the client IP for the login notification
     * @return {@code 200 OK} with a JSON body containing the JWT
     * @throws InvalidTokenException if token generation or persistence fails
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
        return userService.login(loginRequest, request);
    }

    /**
     * Generates an OTP and sends it to the user's registered email address.
     *
     * @param otpRequest request containing the user identifier (email or account number)
     * @return {@code 200 OK} on successful OTP dispatch, or {@code 500} on email failure
     */
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        return userService.generateOtp(otpRequest);
    }

    /**
     * Validates the OTP and, on success, issues a JWT for the authenticated user.
     *
     * @param otpVerificationRequest request containing the identifier and OTP
     * @return {@code 200 OK} with a JSON body containing the JWT
     * @throws InvalidTokenException if token generation or persistence fails
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtpAndLogin(@RequestBody OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {

        return userService.verifyOtpAndLogin(otpVerificationRequest);
    }

    /**
     * Updates the profile of the currently authenticated user.
     * The user's current password must be supplied for identity verification.
     *
     * @param user the new user details (the {@code password} field is used for verification, not updated)
     * @return {@code 200 OK} with an updated {@link com.webapp.bankingportal.dto.UserResponse UserResponse} JSON body
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Invalidates the JWT in the Authorization header and redirects to the logout page.
     *
     * @param token the full Authorization header value (must start with {@code "Bearer "})
     * @return a redirect {@link ModelAndView} to {@code /logout}
     * @throws InvalidTokenException if the token is not found or is invalid
     */
    @GetMapping("/logout")
    public ModelAndView logout(@RequestHeader("Authorization") String token)
            throws InvalidTokenException {

        return userService.logout(token);
    }

}
