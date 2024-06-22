package com.webapp.bankingportal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.service.TokenService;

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
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("User is already authenticated");

            filterChain.doFilter(request, response);
            return;
        }

        String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!requestTokenHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token must start with 'Bearer '");

            return;
        }

        String token = requestTokenHeader.substring(7);
        String username = null;

        try {
            tokenService.validateToken(token);
            username = tokenService.getUsernameFromToken(token);

        } catch (InvalidTokenException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage());
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
