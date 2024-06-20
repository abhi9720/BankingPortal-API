package com.webapp.bankingportal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.webapp.bankingportal.service.EmailService;

@Configuration
public class TestConfig {

    @Bean
    @Primary
    public EmailService emailService() {
        return new EmailServiceMock();
    }
}
