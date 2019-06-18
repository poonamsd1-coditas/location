package com.location.location.config;

public class ErrorCodes {
    private ErrorCodes() {}
    public static final String LOCATION_POPULATED = "Locations have been populated";
    public static final String LOCATION_NOT_POPULATED = "Locations have not been populated";
    public static final String INVALID_QUERY = "Query entered is invalid";
    public static final String FAILED_GEOCODE = "FourSquare couldn't get geocode param";
    public static final String INVALID_AUTH = "FourSquare credentials are invalid";
    public static final String GOOGLE_KEY_MISSING = "Google Geocode API key is invalid";
}
