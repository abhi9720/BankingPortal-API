package com.webapp.bankingportal.service;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.webapp.bankingportal.dto.LoginRequest;
import com.webapp.bankingportal.dto.OtpRequest;
import com.webapp.bankingportal.dto.OtpVerificationRequest;
import com.webapp.bankingportal.dto.UserResponse;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.exception.PasswordResetException;
import com.webapp.bankingportal.exception.UnauthorizedException;
import com.webapp.bankingportal.exception.UserInvalidException;
import com.webapp.bankingportal.mapper.UserMapper;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.JsonUtil;
import com.webapp.bankingportal.util.LoggedinUser;
import com.webapp.bankingportal.util.ValidationUtil;
import com.webapp.bankingportal.util.ApiMessages;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final GeolocationService geolocationService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    @Override
    public ResponseEntity<String> registerUser(User user) {
        validationUtil.validateNewUser(user);
        encodePassword(user);
        val savedUser = saveUserWithAccount(user);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    public ResponseEntity<String> login(LoginRequest loginRequest, HttpServletRequest request)
            throws InvalidTokenException {
        val user = authenticateUser(loginRequest);
        sendLoginNotification(user, request.getRemoteAddr());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> generateOtp(OtpRequest otpRequest) {
        val user = getUserByIdentifier(otpRequest.identifier());
        val otp = otpService.generateOTP(user.getAccount().getAccountNumber());
        return sendOtpEmail(user, otp);
    }

    @Override
    public ResponseEntity<String> verifyOtpAndLogin(OtpVerificationRequest otpVerificationRequest)
            throws InvalidTokenException {
        validateOtpRequest(otpVerificationRequest);
        val user = getUserByIdentifier(otpVerificationRequest.identifier());
        validateOtp(user, otpVerificationRequest.otp());
        val token = generateAndSaveToken(user.getAccount().getAccountNumber());
        return ResponseEntity.ok(String.format(ApiMessages.TOKEN_ISSUED_SUCCESS.getMessage(), token));
    }

    @Override
    public ResponseEntity<String> updateUser(User updatedUser) {
        val accountNumber = LoggedinUser.getAccountNumber();
        authenticateUser(accountNumber, updatedUser.getPassword());
        val existingUser = getUserByAccountNumber(accountNumber);
        updateUserDetails(existingUser, updatedUser);
        val savedUser = saveUser(existingUser);
        return ResponseEntity.ok(JsonUtil.toJson(new UserResponse(savedUser)));
    }

    @Override
    @Transactional
    public boolean resetPassword(User user, String newPassword) {
        try {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new PasswordResetException(ApiMessages.PASSWORD_RESET_FAILURE.getMessage(), e);
        }
    }

    @Override
    public ModelAndView logout(String token) throws InvalidTokenException {
        token = token.substring(7);
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);

        log.info("User logged out successfully {}", tokenService.getUsernameFromToken(token));

        return new ModelAndView("redirect:/logout");
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserByIdentifier(String identifier) {
        User user = null;

        if (validationUtil.doesEmailExist(identifier)) {
            user = getUserByEmail(identifier);
        } else if (validationUtil.doesAccountExist(identifier)) {
            user = getUserByAccountNumber(identifier);
        } else {
            throw new UserInvalidException(
                    String.format(ApiMessages.USER_NOT_FOUND_BY_IDENTIFIER.getMessage(), identifier));
        }

        return user;
    }

    @Override
    public User getUserByAccountNumber(String accountNo) {
        return userRepository.findByAccountAccountNumber(accountNo).orElseThrow(
                () -> new UserInvalidException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNo)));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserInvalidException(String.format(ApiMessages.USER_NOT_FOUND_BY_EMAIL.getMessage(), email)));
    }

    private void encodePassword(User user) {
        user.setCountryCode(user.getCountryCode().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private User saveUserWithAccount(User user) {
        val savedUser = saveUser(user);
        savedUser.setAccount(accountService.createAccount(savedUser));
        return saveUser(savedUser);
    }

    private User authenticateUser(LoginRequest loginRequest) {
        val user = getUserByIdentifier(loginRequest.identifier());
        val accountNumber = user.getAccount().getAccountNumber();
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(accountNumber, loginRequest.password()));
        return user;
    }

    private void authenticateUser(String accountNumber, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountNumber, password));
    }

    private String generateAndSaveToken(String accountNumber) throws InvalidTokenException {
        val userDetails = userDetailsService.loadUserByUsername(accountNumber);
        val token = tokenService.generateToken(userDetails);
        tokenService.saveToken(token);
        return token;
    }

    private ResponseEntity<String> sendOtpEmail(User user, String otp) {
        val emailSendingFuture = otpService.sendOTPByEmail(
                user.getEmail(), user.getName(), user.getAccount().getAccountNumber(), otp);

        ResponseEntity<String> successResponse = ResponseEntity
                .ok(String.format(ApiMessages.OTP_SENT_SUCCESS.getMessage(), user.getEmail()));
        ResponseEntity<String> failureResponse = ResponseEntity.internalServerError()
                .body(String.format(ApiMessages.OTP_SENT_FAILURE.getMessage(), user.getEmail()));

        return emailSendingFuture.thenApply(result -> successResponse)
                .exceptionally(e -> failureResponse).join();
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
        if (request.identifier() == null || request.identifier().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.IDENTIFIER_MISSING_ERROR.getMessage());
        }
        if (request.otp() == null || request.otp().isEmpty()) {
            throw new IllegalArgumentException(ApiMessages.OTP_MISSING_ERROR.getMessage());
        }
    }

    private void validateOtp(User user, String otp) {
        if (!otpService.validateOTP(user.getAccount().getAccountNumber(), otp)) {
            throw new UnauthorizedException(ApiMessages.OTP_INVALID_ERROR.getMessage());
        }
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
        ValidationUtil.validateUserDetails(updatedUser);
        updatedUser.setPassword(existingUser.getPassword());
        userMapper.updateUser(updatedUser, existingUser);
    }

    private CompletableFuture<Boolean> sendLoginNotification(User user, String ip) {
        val loginTime = new Timestamp(System.currentTimeMillis()).toString();

        return geolocationService.getGeolocation(ip)
                .thenComposeAsync(geolocationResponse -> {
                    val loginLocation = String.format("%s, %s",
                            geolocationResponse.getCity().getNames().get("en"),
                            geolocationResponse.getCountry().getNames().get("en"));
                    return sendLoginEmail(user, loginTime, loginLocation);
                })
                .exceptionallyComposeAsync(throwable -> sendLoginEmail(user, loginTime, "Unknown"));
    }

    private CompletableFuture<Boolean> sendLoginEmail(User user, String loginTime, String loginLocation) {
        val emailText = emailService.getLoginEmailTemplate(user.getName(), loginTime, loginLocation);
        return emailService.sendEmail(user.getEmail(), ApiMessages.EMAIL_SUBJECT_LOGIN.getMessage(), emailText)
                .thenApplyAsync(result -> true)
                .exceptionally(ex -> false);
    }

}
