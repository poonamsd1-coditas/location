package com.location.location.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CustomResponseDTO {
    HttpStatus status;
    String message;
    List<VenuesDTO> locations;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
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
