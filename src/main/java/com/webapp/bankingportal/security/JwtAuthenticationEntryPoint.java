package com.webapp.bankingportal.security;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.bankingportal.dto.ErrorResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
     * Component class serving as the entry point for JWT authentication failures.
     * */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        System.out.println("Unauthorized user");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        String errorMessage = "Unauthorized Access";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
