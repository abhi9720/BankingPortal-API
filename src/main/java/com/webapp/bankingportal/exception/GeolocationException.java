package com.webapp.bankingportal.exception;

/**
 * Thrown when the external geolocation API call fails or returns an unusable response.
 *
 * <p>Mapped to HTTP 400 Bad Request by {@link com.webapp.bankingportal.controller.GlobalExceptionHandler}.</p>
 */
public class GeolocationException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public GeolocationException(String message) {
        super(message);
    }
}
