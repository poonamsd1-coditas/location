package com.location.location.dto;

import org.springframework.http.HttpStatus;
import java.util.Set;

public class CustomResponseDTO {
    HttpStatus status;
    String message;
    Set<VenuesDTO> locations;

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

    public Set<VenuesDTO> getLocations() {
        return locations;
    }

    public void setLocations(Set<VenuesDTO> locations) {
        this.locations = locations;
    }
}
