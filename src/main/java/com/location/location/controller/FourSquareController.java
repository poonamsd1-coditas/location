package com.location.location.controller;

import com.location.location.service.FourSquareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foursquare")
public class FourSquareController {

    @Autowired
    FourSquareService fourSquareService;

    @GetMapping("/getLocation")
    public ResponseEntity<String> getLocation(@RequestParam(value = "query", required = true) String query) {

        return new ResponseEntity<>(fourSquareService.getLocation(query), HttpStatus.OK);


    }

}
