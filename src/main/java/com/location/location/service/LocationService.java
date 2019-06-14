package com.location.location.service;

import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private FourSquareService fourSquareService;

    @Autowired
    private GoogleGeocodeService googleGeocodeService;

    public CustomResponseDTO getLocation(String query, String filter) {
        CustomResponseDTO foursquareResponse = fourSquareService.getLocation(query, filter);
        CustomResponseDTO googleResponse = googleGeocodeService.getLocation(query, filter);

        String error = "";
        String message = "Locations populated";
        CustomResponseDTO mergedResponse = new CustomResponseDTO();
        List<VenuesDTO> venuesDTOList = new ArrayList<>();
        mergedResponse.setLocations(venuesDTOList);

        if (foursquareResponse.getError() == null)
            venuesDTOList.addAll(foursquareResponse.getLocations());
        else {
            error = error.concat(foursquareResponse.getError() + ". ");
        }
        if (googleResponse.getError() == null)
            venuesDTOList.addAll(googleResponse.getLocations());
        else {
            error = error.concat(googleResponse.getError());
        }
        if (!error.isEmpty())
            mergedResponse.setError(error);
        if (foursquareResponse.getError() != null && googleResponse.getError() != null)
            message = "Locations not populated";
        mergedResponse.setMessage(message);

        return mergedResponse;
    }
}
