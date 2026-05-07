package com.webapp.bankingportal.service;

import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.webapp.bankingportal.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;

/**
 * Service contract for JWT token lifecycle management, also implementing
 * {@link UserDetailsService} so that Spring Security can load user details
 * from the token subject during request authentication.
 */
public interface TokenService extends UserDetailsService {

    /**
     * Generates a signed JWT for the given user using the configured default expiration.
     *
     * @param userDetails the principal whose username becomes the token subject
     * @return a compact, URL-safe JWT string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates a signed JWT for the given user with an explicit expiry timestamp.
     *
     * @param userDetails the principal whose username becomes the token subject
     * @param expiry      the exact timestamp at which the token expires
     * @return a compact, URL-safe JWT string
     */
    String generateToken(UserDetails userDetails, Date expiry);

    /**
     * Extracts the username (account number) from a JWT.
     *
     * @param token the compact JWT string
     * @return the subject claim, which is the account number
     * @throws InvalidTokenException if the token is expired, malformed, or otherwise invalid
     */
    String getUsernameFromToken(String token) throws InvalidTokenException;

    /**
     * Extracts the expiration timestamp from a JWT.
     *
     * @param token the compact JWT string
     * @return the expiration {@link Date}
     * @throws InvalidTokenException if the token is expired, malformed, or otherwise invalid
     */
    Date getExpirationDateFromToken(String token) throws InvalidTokenException;

    /**
     * Extracts an arbitrary claim from a JWT using the provided resolver function.
     *
     * @param <T>            the type of the resolved claim value
     * @param token          the compact JWT string
     * @param claimsResolver a function that maps the full {@link Claims} object to the desired value
     * @return the resolved claim value
     * @throws InvalidTokenException if the token is expired, malformed, or otherwise invalid
     */
    <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws InvalidTokenException;

    /**
     * Persists a newly generated JWT to the database for server-side revocation tracking.
     *
     * @param token the compact JWT string to persist
     * @throws InvalidTokenException if the token is already present in the database or is invalid
     */
    void saveToken(String token) throws InvalidTokenException;

    /**
     * Validates that the given JWT exists in the database (i.e., has not been revoked).
     *
     * @param token the compact JWT string to validate
     * @throws InvalidTokenException if the token is not found in the database
     */
    void validateToken(String token) throws InvalidTokenException;

    /**
     * Removes the given JWT from the database, effectively revoking the session.
     * If the token is not found, the call is silently ignored.
     *
     * @param token the compact JWT string to invalidate
     */
    void invalidateToken(String token);
}
