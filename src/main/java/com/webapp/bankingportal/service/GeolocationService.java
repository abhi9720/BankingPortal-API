package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

import com.webapp.bankingportal.dto.GeolocationResponse;

public interface GeolocationService {

    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip);
}
