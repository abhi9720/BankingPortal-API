package com.webapp.bankingportal.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.webapp.bankingportal.dto.GeolocationResponse;
import com.webapp.bankingportal.exception.GeolocationException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link GeolocationService}.
 *
 * <p>Calls the external IP geolocation REST API configured via {@code geo.api.url}
 * and {@code geo.api.key} to resolve a given IP address to a city and country.
 * The call is made asynchronously so it does not block the login response path.</p>
 */
@Service
@Slf4j
public class GeolocationServiceImpl implements GeolocationService {

    /** Base URL of the external geolocation API (injected from application properties). */
    @Value("${geo.api.url}")
    private String apiUrl;

    /** API key for authenticating with the external geolocation service. */
    @Value("${geo.api.key}")
    private String apiKey;

    @Override
    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip) {
        val future = new CompletableFuture<GeolocationResponse>();

        try {
            // Validate IP address
            InetAddress.getByName(ip);

            log.info("Getting geolocation for IP: {}", ip);

            // Call geolocation API
            val url = String.format("%s/%s/?token=%s", apiUrl, ip, apiKey);
            val response = new RestTemplate()
                    .getForObject(url, GeolocationResponse.class);

            if (response == null) {
                log.error("Failed to get geolocation for IP: {}", ip);
                future.completeExceptionally(new GeolocationException(
                        "Failed to get geolocation for IP: " + ip));
            } else {
                future.complete(response);
            }

        } catch (UnknownHostException e) {
            log.error("Invalid IP address: {}", ip, e);
            future.completeExceptionally(e);

        } catch (RestClientException e) {
            log.error("Failed to get geolocation for IP: {}", ip, e);
            future.completeExceptionally(e);
        }

        return future;
    }

}
