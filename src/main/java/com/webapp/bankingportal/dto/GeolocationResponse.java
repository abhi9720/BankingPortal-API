package com.webapp.bankingportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Map;
import java.util.List;

/**
 * Response DTO that maps the JSON payload returned by the external IP
 * geolocation API.
 *
 * <p>Used by {@link com.webapp.bankingportal.service.GeolocationService} to
 * determine a user's approximate geographic location at login time, which is
 * then included in the login-notification email.</p>
 */
@Data
public class GeolocationResponse {

    /** City-level information for the resolved IP address. */
    @Data
    public static class City {

        /** GeoNames database identifier for this city. */
        @JsonProperty("geoname_id")
        private int geonameId;

        /** Localised city name strings keyed by ISO 639-1 language code (e.g., {@code "en"}). */
        private Map<String, String> names;

    }

    /** Continent-level information for the resolved IP address. */
    @Data
    public static class Continent {

        /** Two-letter continent code (e.g., {@code "NA"} for North America). */
        private String code;

        /** GeoNames database identifier for this continent. */
        @JsonProperty("geoname_id")
        private int geonameId;

        /** Localised continent name strings keyed by ISO 639-1 language code. */
        private Map<String, String> names;

    }

    /** Country-level information for the resolved IP address. */
    @Data
    public static class Country {

        /** GeoNames database identifier for this country. */
        @JsonProperty("geoname_id")
        private int geonameId;

        /** Whether the country is a member of the European Union. */
        @JsonProperty("is_in_european_union")
        private boolean isInEuropeanUnion;

        /** ISO 3166-1 alpha-2 country code (e.g., {@code "US"}). */
        @JsonProperty("iso_code")
        private String isoCode;

        /** Localised country name strings keyed by ISO 639-1 language code. */
        private Map<String, String> names;

    }

    /** Geographic coordinates and timezone for the resolved IP address. */
    @Data
    public static class Location {

        /** Latitude of the estimated location. */
        private double latitude;

        /** Longitude of the estimated location. */
        private double longitude;

        /** IANA timezone identifier (e.g., {@code "America/New_York"}). */
        @JsonProperty("time_zone")
        private String timeZone;

        /** Weather code for the estimated location, if provided by the API. */
        @JsonProperty("weather_code")
        private String weatherCode;

    }

    /** Postal code information for the resolved IP address. */
    @Data
    public static class Postal {

        /** Postal/ZIP code string for the estimated location. */
        private String code;

    }

    /** Administrative subdivision (state/province) information. */
    @Data
    public static class Subdivision {

        /** GeoNames database identifier for this subdivision. */
        @JsonProperty("geoname_id")
        private int geonameId;

        /** ISO 3166-2 subdivision code. */
        @JsonProperty("iso_code")
        private String isoCode;

        /** Localised subdivision name strings keyed by ISO 639-1 language code. */
        private Map<String, String> names;

    }

    /** ISP and network traits for the resolved IP address. */
    @Data
    public static class Traits {

        /** Autonomous System Number (ASN) for the IP. */
        @JsonProperty("autonomous_system_number")
        private int autonomousSystemNumber;

        /** Organization that owns the Autonomous System. */
        @JsonProperty("autonomous_system_organization")
        private String autonomousSystemOrganization;

        /** Connection type (e.g., {@code "Corporate"}, {@code "Residential"}). */
        @JsonProperty("connection_type")
        private String connectionType;

        /** Internet Service Provider name. */
        private String isp;

        /** Classification of the IP user type (e.g., {@code "business"}). */
        @JsonProperty("user_type")
        private String userType;

    }

    /** City-level information. */
    private City city;

    /** Continent-level information. */
    private Continent continent;

    /** Country-level information. */
    private Country country;

    /** Geographic coordinates and timezone. */
    private Location location;

    /** Postal code information. */
    private Postal postal;

    /** List of administrative subdivisions (e.g., states/provinces). */
    private List<Subdivision> subdivisions;

    /** ISP and network trait information. */
    private Traits traits;

}
