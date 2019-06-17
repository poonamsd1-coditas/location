package com.location.location.controller;

import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            responseDTO.setError("Query string is empty. Enter valid query");
            responseDTO.setMessage("Locations not populated");
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(locationService.getLocation(query, filter), HttpStatus.OK);
    }

}
