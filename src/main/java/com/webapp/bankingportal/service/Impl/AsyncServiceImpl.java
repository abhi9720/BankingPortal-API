package com.webapp.bankingportal.service.Impl;

import com.webapp.bankingportal.service.AsyncService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncServiceImpl implements AsyncService {
    @Override
    public CompletableFuture<String> asyncInvokeWithCompletableFuture() {
        return null;
    }
}
