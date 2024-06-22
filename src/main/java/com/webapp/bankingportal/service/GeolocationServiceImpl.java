package com.webapp.bankingportal.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.webapp.bankingportal.dto.GeolocationResponse;
import com.webapp.bankingportal.exception.GeolocationException;

@Service
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${geo.api.url}")
    private String apiUrl;

    @Value("${geo.api.key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(GeolocationServiceImpl.class);

    @Override
    @Async
    public CompletableFuture<GeolocationResponse> getGeolocation(String ip) {
        CompletableFuture<GeolocationResponse> future = new CompletableFuture<>();

        try {
            // Validate IP address
            InetAddress.getByName(ip);

            logger.info("Getting geolocation for IP: {}", ip);

            // Call geolocation API
            String url = String.format("%s/%s/?token=%s", apiUrl, ip, apiKey);
            GeolocationResponse response = new RestTemplate()
                    .getForObject(url, GeolocationResponse.class);

            if (response == null) {
                logger.error("Failed to get geolocation for IP: {}", ip);
                future.completeExceptionally(new GeolocationException(
                        "Failed to get geolocation for IP: " + ip));
            } else {
                future.complete(response);
            }

        } catch (UnknownHostException e) {
            logger.error("Invalid IP address: {}", ip, e);
            future.completeExceptionally(e);

        } catch (RestClientException e) {
            logger.error("Failed to get geolocation for IP: {}", ip, e);
            future.completeExceptionally(e);
        }

        return future;
    }
}
