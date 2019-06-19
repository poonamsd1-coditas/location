package com.location.location.service;

import com.location.location.config.AppConstants;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private GeoLocationService fourSquareService;

    @Autowired
    private GeoLocationService googleGeocodeService;

    public ResponseEntity<CustomResponseDTO> getLocation(String query, String filter) {
        log.info("Request to get places for a location:{} with filters: {}", query, filter);
        CustomResponseDTO foursquareResponse = fourSquareService.getLocation(query, filter);
        CustomResponseDTO googleResponse = googleGeocodeService.getLocation(query, filter);

        CustomResponseDTO mergedResponse = new CustomResponseDTO();
        Set<VenuesDTO> venuesDTOSet = new HashSet<>();
        mergedResponse.setLocations(venuesDTOSet);

        if (foursquareResponse.getStatus().equals(HttpStatus.OK) && googleResponse.getStatus().equals(HttpStatus.OK)) {
            venuesDTOSet.addAll(foursquareResponse.getLocations());
            venuesDTOSet.addAll(googleResponse.getLocations());
            mergedResponse.setStatus(HttpStatus.OK);
            mergedResponse.setMessage(AppConstants.LOCATION_POPULATED);
            mergedResponse.setLocations(venuesDTOSet);
        }
        else if (foursquareResponse.getStatus().equals(HttpStatus.OK))
            mergedResponse = foursquareResponse;
        else if (googleResponse.getStatus().equals(HttpStatus.OK))
            mergedResponse = googleResponse;
        else {
            log.error("No locations populated");
            mergedResponse.setStatus(HttpStatus.BAD_REQUEST);
            mergedResponse.setMessage(AppConstants.LOCATION_NOT_POPULATED);
            return new ResponseEntity<>(mergedResponse, mergedResponse.getStatus());
        }
        return new ResponseEntity<>(mergedResponse, mergedResponse.getStatus());
    }
}
