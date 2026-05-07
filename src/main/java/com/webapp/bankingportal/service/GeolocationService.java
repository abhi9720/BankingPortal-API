package com.webapp.bankingportal.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;

import com.webapp.bankingportal.dto.GeolocationResponse;

/**
 * Service contract for resolving an IP address to a geographic location.
 */
public interface GeolocationService {

    /**
     * Resolves the city and country for the given IP address by calling the
     * external geolocation API asynchronously.
     *
     * @param ip the IPv4 or IPv6 address to resolve
     * @return a {@link CompletableFuture} containing the {@link GeolocationResponse},
     *         or completing exceptionally if the IP is invalid or the API call fails
     */
    @Async
    CompletableFuture<GeolocationResponse> getGeolocation(String ip);
}
