package com.location.location.dto;

import java.util.List;

public class CustomResponseDTO {
    String error;
    String message;
    List<VenuesDTO> locations;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<VenuesDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<VenuesDTO> locations) {
        this.locations = locations;
    }
}
