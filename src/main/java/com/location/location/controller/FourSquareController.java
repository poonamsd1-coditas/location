package com.location.location.controller;

import com.location.location.dto.CustomResponseDTO;
import com.location.location.service.impl.FourSquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foursquare")
public class FourSquareController {

    @Autowired
    private FourSquareService fourSquareService;

    @GetMapping("/getLocation")
    public ResponseEntity<CustomResponseDTO> getLocation(@RequestParam(value = "query", required = true) String query,
                                                         @RequestParam(value = "filter", required = false) String filter) {
        CustomResponseDTO responseDTO = fourSquareService.getLocation(query, filter);
        return new ResponseEntity<>(responseDTO, responseDTO.getStatus());
    }

}
