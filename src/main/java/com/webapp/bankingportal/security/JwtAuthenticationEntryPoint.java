package com.webapp.bankingportal.security;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/*
	 * Component class serving as the entry point for JWT authentication failures.
	 * */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		System.out.println("Un Authorised user");
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
	}

}
