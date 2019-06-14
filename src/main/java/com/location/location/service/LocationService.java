package com.location.location.service;

import com.location.location.dto.VenuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private FourSquareService fourSquareService;

    @Autowired
    private GoogleGeocodeService googleGeocodeService;

    public List<VenuesDTO> getLocation(String query, String filter) {

        List<VenuesDTO> venuesDTOList = fourSquareService.getLocation(query, filter);
        venuesDTOList.addAll(googleGeocodeService.getLocation(query, filter));

        return venuesDTOList;
    }
}
