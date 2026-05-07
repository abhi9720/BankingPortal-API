package com.webapp.bankingportal.service;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.entity.Token;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TokenRepository;
import com.webapp.bankingportal.repository.UserRepository;
import com.webapp.bankingportal.util.ApiMessages;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link TokenService}.
 *
 * <p>Handles JWT creation, parsing, persistence, and revocation using the
 * JJWT library. Tokens are signed with HMAC-SHA-512 using a Base64-encoded
 * secret configured via {@code jwt.secret}. Expiry is configured via
 * {@code jwt.expiration} (milliseconds).</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    /** Base64-encoded HMAC-SHA-512 signing secret injected from application properties. */
    @Value("${jwt.secret}")
    private String secret;

    /** Token lifetime in milliseconds, injected from application properties. */
    @Value("${jwt.expiration}")
    private long expiration;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AccountRepository accountRepository;

    @Override
    public String getUsernameFromToken(String token) throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: " + userDetails.getUsername());
        return doGenerateToken(userDetails,
                new Date(System.currentTimeMillis() + expiration));
    }

    @Override
    public String generateToken(UserDetails userDetails, Date expiry) {
        log.info("Generating token for user: " + userDetails.getUsername());
        return doGenerateToken(userDetails, expiry);
    }
    /**
     * Decodes the Base64 secret and returns the HMAC-SHA signing key.
     *
     * @return the {@link Key} used to sign and verify JWTs
     */
    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Builds and signs a JWT with the given principal and expiry time.
     *
     * @param userDetails the principal whose username becomes the subject claim
     * @param expiry      the exact expiration timestamp
     * @return a compact, signed JWT string
     */
    private String doGenerateToken(UserDetails userDetails, Date expiry) {
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(key(), SignatureAlgorithm.HS512).compact();
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        val user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ApiMessages.USER_NOT_FOUND_BY_ACCOUNT.getMessage(), accountNumber)));

        return withUsername(accountNumber).password(user.getPassword()).build();
    }

    @Override
    public Date getExpirationDateFromToken(String token)
            throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    @Override
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver)
            throws InvalidTokenException {
        val claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Builds a {@link JwtParser} configured with the application's signing key.
     *
     * @return a ready-to-use JWT parser
     */
    private JwtParser parser() {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build();
    }

    /**
     * Parses the JWT and returns all claims. Translates JJWT-specific exceptions
     * into {@link InvalidTokenException}.
     *
     * @param token the compact JWT string to parse
     * @return the full {@link Claims} payload
     * @throws InvalidTokenException if the token is expired, unsupported, malformed,
     *                               has an invalid signature, or is empty
     */
    private Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
        try {
           // return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            return Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
          //  return parser().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // Delete expired token
            invalidateToken(token);

            throw new InvalidTokenException(ApiMessages.TOKEN_EXPIRED_ERROR.getMessage());

        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_UNSUPPORTED_ERROR.getMessage());

        } catch (MalformedJwtException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_MALFORMED_ERROR.getMessage());

        } catch (SignatureException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_SIGNATURE_INVALID_ERROR.getMessage());

        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ApiMessages.TOKEN_EMPTY_ERROR.getMessage());
        }
    }

    @Override
    public void saveToken(String token) throws InvalidTokenException {
        if (tokenRepository.findByToken(token) != null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_ALREADY_EXISTS_ERROR.getMessage());
        }

        val account = accountRepository.findByAccountNumber(
                getUsernameFromToken(token));

        log.info("Saving token for account: " + account.getAccountNumber());

        val tokenObj = new Token(
                token,
                getExpirationDateFromToken(token),
                account);

        tokenRepository.save(tokenObj);
    }

    @Override
    public void validateToken(String token) throws InvalidTokenException {
        if (tokenRepository.findByToken(token) == null) {
            throw new InvalidTokenException(ApiMessages.TOKEN_NOT_FOUND_ERROR.getMessage());
        }
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        if (tokenRepository.findByToken(token) != null) {
            tokenRepository.deleteByToken(token);
        }
    }

}
