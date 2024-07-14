package com.webapp.bankingportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Map;
import java.util.List;

@Data
public class GeolocationResponse {

    @Data
    public static class City {

        @JsonProperty("geoname_id")
        private int geonameId;

        private Map<String, String> names;

    }

    @Data
    public static class Continent {

        private String code;

        @JsonProperty("geoname_id")
        private int geonameId;

        private Map<String, String> names;

    }

    @Data
    public static class Country {

        @JsonProperty("geoname_id")
        private int geonameId;

        @JsonProperty("is_in_european_union")
        private boolean isInEuropeanUnion;

        @JsonProperty("iso_code")
        private String isoCode;

        private Map<String, String> names;

    }

    @Data
    public static class Location {

        private double latitude;
        private double longitude;

        @JsonProperty("time_zone")
        private String timeZone;

        @JsonProperty("weather_code")
        private String weatherCode;

    }

    @Data
    public static class Postal {

        private String code;

    }

    @Data
    public static class Subdivision {

        @JsonProperty("geoname_id")
        private int geonameId;

        @JsonProperty("iso_code")
        private String isoCode;

        private Map<String, String> names;

    }

    @Data
    public static class Traits {

        @JsonProperty("autonomous_system_number")
        private int autonomousSystemNumber;

        @JsonProperty("autonomous_system_organization")
        private String autonomousSystemOrganization;

        @JsonProperty("connection_type")
        private String connectionType;

        private String isp;

        @JsonProperty("user_type")
        private String userType;

    }

    private City city;
    private Continent continent;
    private Country country;
    private Location location;
    private Postal postal;
    private List<Subdivision> subdivisions;
    private Traits traits;

}
