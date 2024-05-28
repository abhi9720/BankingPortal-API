package com.webapp.bankingportal.security;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationFilter is responsible for filtering incoming requests to
 * authenticate JWT tokens. It extracts the JWT token from the Authorization
 * header, validates it, and sets the authentication in the
 * SecurityContextHolder if the token is valid. If the token is invalid or
 * expired, appropriate log messages are generated. This filter is used for
 * securing endpoints that require JWT authentication.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("Request Headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                logger.debug("\t" + name + ": " + request.getHeader(name));
            }
        }

        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String token;

        if (requestTokenHeader == null) {
            logger.info("No JWT Token found in Request Headers");
        } else if (!requestTokenHeader.startsWith("Bearer")) {
            logger.info("JWT header does not begin with 'Bearer' prefix");
            logger.info("JWT header: " + requestTokenHeader);
        } else {
            token = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Unable to get token");
            } catch (ExpiredJwtException e) {
                logger.info("JWT Token Expired");
            } catch (MalformedJwtException e) {
                logger.info("Malformed JWT Token");
            }

            if (username == null) {
                logger.info("User not found in JWT Token");
            } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.info("User is already authenticated");
            } else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    logger.info("Valid JWT Token for account: " + username);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.info("Invalid JWT Token");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
