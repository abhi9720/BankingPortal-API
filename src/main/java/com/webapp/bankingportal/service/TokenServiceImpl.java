package com.webapp.bankingportal.service;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.util.Date;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.bankingportal.entity.Account;
import com.webapp.bankingportal.entity.Token;
import com.webapp.bankingportal.entity.User;
import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.repository.AccountRepository;
import com.webapp.bankingportal.repository.TokenRepository;
import com.webapp.bankingportal.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final long expiration;
    private final String secret;

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    public TokenServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secret = secret;
        this.expiration = expiration;
    }

    @Override
    public String getUsernameFromToken(String token) throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        logger.info("Generating token for user: " + userDetails.getUsername());
        return doGenerateToken(userDetails);
    }

    private String doGenerateToken(UserDetails userDetails) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + expiration))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        User user = userRepository.findByAccountAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with account number: " + accountNumber));

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
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        } catch (ExpiredJwtException e) {
            // Delete expired token
            invalidateToken(token);

            throw new InvalidTokenException("Token has expired");

        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Token is not supported");

        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Token is malformed");

        } catch (SignatureException e) {
            throw new InvalidTokenException("Token signature is invalid");

        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token is empty");
        }
    }

    @Override
    public void saveToken(String token) throws InvalidTokenException {
        if (tokenRepository.findByToken(token) != null) {
            throw new InvalidTokenException("Token already exists");
        }

        Account account = accountRepository.findByAccountNumber(
                getUsernameFromToken(token));

        logger.info("Saving token for account: " + account.getAccountNumber());

        Token tokenObj = new Token(
                token,
                getExpirationDateFromToken(token),
                account);

        tokenRepository.save(tokenObj);
    }

    @Override
    public void validateToken(String token) throws InvalidTokenException {
        if (tokenRepository.findByToken(token) == null) {
            throw new InvalidTokenException("Token not found");
        }

        String accountNumber = getUsernameFromToken(token);

        if (accountRepository.findByAccountNumber(accountNumber) == null) {
            throw new InvalidTokenException("Token account not found");
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
