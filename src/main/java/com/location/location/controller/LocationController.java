package com.location.location.controller;

import com.location.location.config.AppConstants;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**@author : Poonam Doddamani
 * Controller with APIs defined to list locations, search and filter
 */
@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    /**
     * Get list of places for a location with respective filters applied
     *
     * @param query -- search string to find location e.g. Pune
     * @param filter -- category/type to filter out the search results e.g. bank
     * @return List of VenuesDTO in CustomResponseDTO
     */
    @GetMapping("/getLocation")
    public ResponseEntity<CustomResponseDTO> getLocationByPlace(@RequestParam(value = "query", required = true) String query,
                                                         @RequestParam(value = "filter", required = false) String filter) {
        if (!StringUtils.isEmpty(query)) {
            LOGGER.info("Request to location service to get location");
            return locationService.getLocation(query, filter);
        }
        else {
            LOGGER.error("Invalid query entered");
            CustomResponseDTO responseDTO = new CustomResponseDTO();
            responseDTO.setStatus(HttpStatus.BAD_REQUEST);
            responseDTO.setMessage(AppConstants.INVALID_QUERY);
            return new ResponseEntity<>(responseDTO, responseDTO.getStatus());
        }
    }

}
