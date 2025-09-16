package com.webapp.bankingportal.controller;

import com.webapp.bankingportal.service.AsyncService;
import com.webapp.bankingportal.service.IRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private IRequestService requestService;

    @PostMapping("/testAsyncFuture")
    public String testAsyncFutureWithCompletableFuture() {
        CompletableFuture<String> completableFuture = asyncService.asyncInvokeWithCompletableFuture();
        return completableFuture.join();
    }

    @GetMapping("/testGetRequestAttr")
    public String testGetRequestAttr() throws InterruptedException, ExecutionException{
        return requestService.getRequestAttr();
    }
}
