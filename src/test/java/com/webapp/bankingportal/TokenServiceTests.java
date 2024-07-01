package com.webapp.bankingportal;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.repository.TokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenServiceTests extends BaseTest {

    @Autowired
    TokenRepository tokenRepository;

    @Test
    public void test_validate_token_with_valid_token() throws Exception {
        String token = createAndLoginUser().get("token");
        tokenService.validateToken(token);
        tokenService.invalidateToken(token);
    }

    @Test
    public void test_validate_token_with_invalid_token() throws Exception {
        String token = generateToken(getRandomAccountNumber(), getRandomPassword());

        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.validateToken(token),
                "Token not found");
    }

    @Test
    public void test_invalidate_token_with_valid_token() throws Exception {
        String token = createAndLoginUser().get("token");
        Assertions.assertNotNull(tokenRepository.findByToken(token));

        tokenService.invalidateToken(token);
        Assertions.assertNull(tokenRepository.findByToken(token));
    }

    @Test
    public void test_invalidate_token_with_invalid_token() throws Exception {
        String token = generateToken(getRandomAccountNumber(), getRandomPassword());
        tokenService.invalidateToken(token);
    }

    @Test
    public void test_save_token_with_valid_token() throws Exception {
        HashMap<String, String> accountDetails = createAccount();
        String token = generateToken(
                accountDetails.get("accountNumber"),
                accountDetails.get("password"));

        tokenService.saveToken(token);
        Assertions.assertNotNull(tokenRepository.findByToken(token));

        tokenService.invalidateToken(token);
        Assertions.assertNull(tokenRepository.findByToken(token));
    }

    @Test
    public void test_save_token_with_duplicate_token() throws Exception {
        String token = createAndLoginUser().get("token");
        Assertions.assertNotNull(tokenRepository.findByToken(token));
        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.saveToken(token),
                "Token already exists");

        tokenService.invalidateToken(token);
        Assertions.assertNull(tokenRepository.findByToken(token));
    }

    @Test
    public void test_get_username_from_token_with_valid_token() throws Exception {
        HashMap<String, String> userDetails = createAndLoginUser();
        String token = userDetails.get("token");
        String accountNumber = userDetails.get("accountNumber");
        String username = tokenService.getUsernameFromToken(token);

        Assertions.assertEquals(accountNumber, username);
    }

    @Test
    public void test_get_username_from_token_with_expired_token() {
        String token = generateToken(
                getRandomAccountNumber(), getRandomPassword(), new Date());

        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.getUsernameFromToken(token),
                "Token has expired");
    }

    @Test
    public void test_get_username_from_token_with_unsigned_token() {
        String token = Jwts.builder().setSubject(getRandomAccountNumber())
                .setIssuedAt(new Date()).compact();

        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.getUsernameFromToken(token),
                "Token is not supported");
    }

    @Test
    public void test_get_username_from_token_with_malformed_token() {
        String token = "malformed";

        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.getUsernameFromToken(token),
                "Token is malformed");
    }

    @Test
    public void test_get_username_from_token_with_invalid_signature() {
        String token = Jwts.builder().setSubject(getRandomAccountNumber())
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "invalid")
                .compact();

        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.getUsernameFromToken(token),
                "Token signature is invalid");
    }

    @Test
    public void test_get_username_from_token_with_empty_token() {
        Assertions.assertThrows(InvalidTokenException.class,
                () -> tokenService.getUsernameFromToken(null),
                "Token is empty");
    }
}
