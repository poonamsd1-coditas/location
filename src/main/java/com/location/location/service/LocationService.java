package com.location.location.service;

import com.location.location.config.ErrorCodes;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private FourSquareService fourSquareService;

    @Autowired
    private GoogleGeocodeService googleGeocodeService;

    public ResponseEntity<CustomResponseDTO> getLocation(String query, String filter) {
        CustomResponseDTO foursquareResponse = fourSquareService.getLocation(query, filter);
        CustomResponseDTO googleResponse = googleGeocodeService.getLocation(query, filter);

        CustomResponseDTO mergedResponse = new CustomResponseDTO();
        List<VenuesDTO> venuesDTOList = new ArrayList<>();
        mergedResponse.setLocations(venuesDTOList);

        if (foursquareResponse.getStatus().equals(HttpStatus.OK) && googleResponse.getStatus().equals(HttpStatus.OK)) {
            venuesDTOList.addAll(foursquareResponse.getLocations());
            venuesDTOList.addAll(googleResponse.getLocations());
            mergedResponse.setStatus(HttpStatus.OK);
            mergedResponse.setMessage(ErrorCodes.LOCATION_POPULATED);
            mergedResponse.setLocations(venuesDTOList);
        }
        else if (foursquareResponse.getStatus().equals(HttpStatus.OK))
            mergedResponse = foursquareResponse;
        else if (googleResponse.getStatus().equals(HttpStatus.OK))
            mergedResponse = googleResponse;
        else {
            mergedResponse.setStatus(HttpStatus.BAD_REQUEST);
            mergedResponse.setMessage(ErrorCodes.LOCATION_NOT_POPULATED);
            return new ResponseEntity<>(mergedResponse, mergedResponse.getStatus());
        }

        return new ResponseEntity<>(mergedResponse, mergedResponse.getStatus());
    }
}
