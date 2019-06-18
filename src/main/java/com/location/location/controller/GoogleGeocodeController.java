package com.location.location.controller;

import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.GoogleGeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/google")
public class GoogleGeocodeController {

    @Autowired
    private GoogleGeocodeService googleGeocodeService;

    @GetMapping("/getLocation")
    public ResponseEntity<CustomResponseDTO> getLocation(@RequestParam(value = "query", required = true) String query,
                                                         @RequestParam(value = "filter", required = false) String filter) {
        CustomResponseDTO responseDTO = googleGeocodeService.getLocation(query, filter);
        return new ResponseEntity<>(responseDTO, responseDTO.getStatus());
    }
}
