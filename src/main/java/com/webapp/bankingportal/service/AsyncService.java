package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

public interface AsyncService {

    CompletableFuture<String> asyncInvokeWithCompletableFuture();
}
