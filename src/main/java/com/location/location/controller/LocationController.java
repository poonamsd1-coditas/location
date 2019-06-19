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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private LocationService locationService;

    @GetMapping("/getLocation")
    public ResponseEntity<CustomResponseDTO> getLocationByPlace(@RequestParam(value = "query", required = true) String query,
                                                         @RequestParam(value = "filter", required = false) String filter) {
        if (!StringUtils.isEmpty(query))
            return locationService.getLocation(query, filter);
        else {
            log.error("Invalid query entered");
            CustomResponseDTO responseDTO = new CustomResponseDTO();
            responseDTO.setStatus(HttpStatus.BAD_REQUEST);
            responseDTO.setMessage(AppConstants.INVALID_QUERY);
            return new ResponseEntity<>(responseDTO, responseDTO.getStatus());
        }
    }

}
