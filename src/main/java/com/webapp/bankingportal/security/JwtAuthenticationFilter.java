package com.webapp.bankingportal.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	/*
	 *  Filter class that handles JWT authentication and authorization for each request.
	 * */

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String token = null;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
			token = requestTokenHeader.substring(7);
			try {
				username = this.jwtTokenUtil.getUsernameFromToken(token);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token Expired");
			} catch (MalformedJwtException e) {
				System.out.println("Malformed JWT Token");
			}

		} else {
			System.out.println("JWT Token does not begin with Bearer String");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (this.jwtTokenUtil.validateToken(token, userDetails)) {

				System.out.println("userDetails.getAuthorities() : " + userDetails.getAuthorities());
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				System.out.println("Invalid JWT Token");
			}

		} else {
			System.out.println("User Not Found or Security Context is not null");
		}

		filterChain.doFilter(request, response);
	}
}
