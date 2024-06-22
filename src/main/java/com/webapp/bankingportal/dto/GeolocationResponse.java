package com.webapp.bankingportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.List;

public class GeolocationResponse {

    public static class City {
        @JsonProperty("geoname_id")
        private int geonameId;

        private Map<String, String> names;

        // Getters and setters
        public int getGeonameId() {
            return geonameId;
        }

        public void setGeonameId(int geonameId) {
            this.geonameId = geonameId;
        }

        public Map<String, String> getNames() {
            return names;
        }

        public void setNames(Map<String, String> names) {
            this.names = names;
        }
    }

    public static class Continent {
        private String code;

        @JsonProperty("geoname_id")
        private int geonameId;

        private Map<String, String> names;

        // Getters and setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getGeonameId() {
            return geonameId;
        }

        public void setGeonameId(int geonameId) {
            this.geonameId = geonameId;
        }

        public Map<String, String> getNames() {
            return names;
        }

        public void setNames(Map<String, String> names) {
            this.names = names;
        }
    }

    public static class Country {
        @JsonProperty("geoname_id")
        private int geonameId;

        @JsonProperty("is_in_european_union")
        private boolean isInEuropeanUnion;

        @JsonProperty("iso_code")
        private String isoCode;

        private Map<String, String> names;

        // Getters and setters
        public int getGeonameId() {
            return geonameId;
        }

        public void setGeonameId(int geonameId) {
            this.geonameId = geonameId;
        }

        public boolean isInEuropeanUnion() {
            return isInEuropeanUnion;
        }

        public void setInEuropeanUnion(boolean isInEuropeanUnion) {
            this.isInEuropeanUnion = isInEuropeanUnion;
        }

        public String getIsoCode() {
            return isoCode;
        }

        public void setIsoCode(String isoCode) {
            this.isoCode = isoCode;
        }

        public Map<String, String> getNames() {
            return names;
        }

        public void setNames(Map<String, String> names) {
            this.names = names;
        }
    }

    public static class Location {
        private double latitude;
        private double longitude;

        @JsonProperty("time_zone")
        private String timeZone;

        @JsonProperty("weather_code")
        private String weatherCode;

        // Getters and setters
        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public String getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(String weatherCode) {
            this.weatherCode = weatherCode;
        }
    }

    public static class Postal {
        private String code;

        // Getters and setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class Subdivision {
        @JsonProperty("geoname_id")
        private int geonameId;

        @JsonProperty("iso_code")
        private String isoCode;

        private Map<String, String> names;

        // Getters and setters
        public int getGeonameId() {
            return geonameId;
        }

        public void setGeonameId(int geonameId) {
            this.geonameId = geonameId;
        }

        public String getIsoCode() {
            return isoCode;
        }

        public void setIsoCode(String isoCode) {
            this.isoCode = isoCode;
        }

        public Map<String, String> getNames() {
            return names;
        }

        public void setNames(Map<String, String> names) {
            this.names = names;
        }
    }

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

        // Getters and setters
        public int getAutonomousSystemNumber() {
            return autonomousSystemNumber;
        }

        public void setAutonomousSystemNumber(int autonomousSystemNumber) {
            this.autonomousSystemNumber = autonomousSystemNumber;
        }

        public String getAutonomousSystemOrganization() {
            return autonomousSystemOrganization;
        }

        public void setAutonomousSystemOrganization(String autonomousSystemOrganization) {
            this.autonomousSystemOrganization = autonomousSystemOrganization;
        }

        public String getConnectionType() {
            return connectionType;
        }

        public void setConnectionType(String connectionType) {
            this.connectionType = connectionType;
        }

        public String getIsp() {
            return isp;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }

    private City city;
    private Continent continent;
    private Country country;
    private Location location;
    private Postal postal;
    private List<Subdivision> subdivisions;
    private Traits traits;

    // Getters and setters
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Postal getPostal() {
        return postal;
    }

    public void setPostal(Postal postal) {
        this.postal = postal;
    }

    public List<Subdivision> getSubdivisions() {
        return subdivisions;
    }

    public void setSubdivisions(List<Subdivision> subdivisions) {
        this.subdivisions = subdivisions;
    }

    public Traits getTraits() {
        return traits;
    }

    public void setTraits(Traits traits) {
        this.traits = traits;
    }
}
