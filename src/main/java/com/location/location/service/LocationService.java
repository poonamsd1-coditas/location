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

/**@author : Poonam Doddamani
 * Service containing methods to obtain locations by search string and filter
 */
@Service
public class LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

    @Autowired
    private GeoLocationService fourSquareService;

    @Autowired
    private GeoLocationService googleGeocodeService;

    /**
     * Get list of places for a location with respective filters applied
     *
     * @param query -- search string to find location e.g. Pune
     * @param filter -- category/type to filter out the search results e.g. bank
     * @return List of VenuesDTO in CustomResponseDTO
     */
    public ResponseEntity<CustomResponseDTO> getLocation(String query, String filter) {
        LOGGER.info("Request to get places for a location:{} with filters: {}", query, filter);
        CustomResponseDTO foursquareResponse = fourSquareService.getLocation(query, filter);
        CustomResponseDTO googleResponse = googleGeocodeService.getLocation(query, filter);

        CustomResponseDTO mergedResponse = new CustomResponseDTO();
        mergedResponse.setStatus(HttpStatus.OK);
        mergedResponse.setMessage(AppConstants.LOCATION_POPULATED);
        Set<VenuesDTO> venuesDTOSet = new HashSet<>();
        if (foursquareResponse.getLocations() != null && !foursquareResponse.getLocations().isEmpty()) {
            venuesDTOSet.addAll(foursquareResponse.getLocations());
        }
        if (googleResponse.getLocations() != null && !googleResponse.getLocations().isEmpty()) {
            venuesDTOSet.addAll(googleResponse.getLocations());
        }
        if (venuesDTOSet.isEmpty()) {
            mergedResponse.setMessage(AppConstants.LOCATION_NOT_POPULATED);
        }
        else {
            mergedResponse.setLocations(venuesDTOSet);
        }
        return new ResponseEntity<>(mergedResponse, mergedResponse.getStatus());
    }
}
