package com.webapp.bankingportal.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service contract for user account registration, authentication, and profile management.
 */
public interface UserService {

    /**
     * Validates, persists, and activates a new user together with an associated bank account.
     *
     * @param user the new user to register (password will be BCrypt-encoded before storage)
     * @return {@code 200 OK} with a {@link com.webapp.bankingportal.dto.UserResponse UserResponse} JSON body
     * @throws com.webapp.bankingportal.exception.UserInvalidException if validation fails or the email/phone already exists
     */
    ResponseEntity<String> registerUser(User user);

    /**
     * Authenticates a user using their password, sends a login-notification email,
     * and issues a JWT.
     *
     * @param loginRequest the login credentials (identifier + password)
     * @param request      the incoming HTTP request, used to extract the client IP for geolocation
     * @return {@code 200 OK} with a JSON body containing the issued JWT
     * @throws InvalidTokenException if token generation or persistence fails
     */
    ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException;

    /**
     * Generates an OTP for the account identified by the request and sends it by email.
     *
     * @param otpRequest request containing the user identifier (email or account number)
     * @return {@code 200 OK} on successful dispatch, or {@code 500} on email failure
     */
    ResponseEntity<String> generateOtp(OtpRequest otpRequest);

    /**
     * Validates an OTP and, on success, issues a JWT for the authenticated user.
     *
     * @param otpVerificationRequest request containing the identifier and OTP
     * @return {@code 200 OK} with a JSON body containing the issued JWT
     * @throws InvalidTokenException if token generation or persistence fails
     * @throws com.webapp.bankingportal.exception.UnauthorizedException if the OTP is invalid
     */
    ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException;

    /**
     * Updates the profile of the currently authenticated user after verifying their password.
     *
     * @param user the updated user data (password field is used for identity verification)
     * @return {@code 200 OK} with a {@link com.webapp.bankingportal.dto.UserResponse UserResponse} JSON body
     */
    ResponseEntity<String> updateUser(User user);

    /**
     * Invalidates the JWT supplied in the Authorization header and redirects to the logout page.
     *
     * @param token the full Authorization header value (must start with {@code "Bearer "})
     * @return a redirect {@link ModelAndView} to {@code /logout}
     * @throws InvalidTokenException if the token is not found or is invalid
     */
    ModelAndView logout(String token) throws InvalidTokenException;

    /**
     * Replaces the user's current password with the given new password (BCrypt-encoded).
     *
     * @param user        the user whose password is to be reset
     * @param newPassword the new plain-text password to encode and store
     * @return {@code true} if the password was successfully reset
     * @throws com.webapp.bankingportal.exception.PasswordResetException if persistence fails
     */
    boolean resetPassword(User user, String newPassword);

    /**
     * Persists (creates or updates) a {@link User} entity in the database.
     *
     * @param user the user to save
     * @return the saved user entity
     */
    User saveUser(User user);

    /**
     * Looks up a user by either their email address or account number.
     *
     * @param identifier the email address or 6-digit account number
     * @return the matching {@link User}
     * @throws com.webapp.bankingportal.exception.UserInvalidException if no user matches the identifier
     */
    User getUserByIdentifier(String identifier);

    /**
     * Looks up a user by their associated account number.
     *
     * @param accountNo the 6-digit account number
     * @return the matching {@link User}
     * @throws com.webapp.bankingportal.exception.UserInvalidException if no user is linked to the account number
     */
    User getUserByAccountNumber(String accountNo);

    /**
     * Looks up a user by their email address.
     *
     * @param email the email address to search for
     * @return the matching {@link User}
     * @throws com.webapp.bankingportal.exception.UserInvalidException if no user has that email
     */
    User getUserByEmail(String email);

}
