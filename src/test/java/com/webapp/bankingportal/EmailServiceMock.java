package com.webapp.bankingportal;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.webapp.bankingportal.service.EmailServiceImpl;

@Service
public class EmailServiceMock extends EmailServiceImpl {

    public EmailServiceMock() {
        super(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null); // Indicate that the email sending is successful

        return future;
    }
}
