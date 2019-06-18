package com.location.location.controller;

import com.location.location.config.ErrorCodes;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/getLocation")
    public ResponseEntity<CustomResponseDTO> getLocation(@RequestParam(value = "query", required = true) String query,
                                                         @RequestParam(value = "filter", required = false) String filter) {
        if (query == null || query.isEmpty()) {
            CustomResponseDTO responseDTO = new CustomResponseDTO();
            responseDTO.setStatus(HttpStatus.BAD_REQUEST);
            responseDTO.setMessage(ErrorCodes.INVALID_QUERY);
            return new ResponseEntity<>(responseDTO, responseDTO.getStatus());
        }
        return locationService.getLocation(query, filter);
    }

}
