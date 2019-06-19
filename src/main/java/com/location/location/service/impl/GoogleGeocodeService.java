package com.location.location.service.impl;

import com.location.location.config.ApplicationProperties;
import com.location.location.config.AppConstants;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.GeoLocationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GoogleGeocodeService implements GeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(GoogleGeocodeService.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public CustomResponseDTO getLocation(String query, String filter) {
        log.info("Request to get places for a location:{} with filters: {}", query, filter);
        if (!StringUtils.isEmpty(filter))
            query = query.concat(" " + filter);
        String url = getGoogleUrl().concat("&address=" + query);
        CustomResponseDTO responseDTO = new CustomResponseDTO();
        String dataUrl = "";
        try {
            dataUrl = getDataJson(url);
            JSONObject dataJson = new JSONObject(dataUrl);
            JSONArray resultArray = dataJson.getJSONArray("results");
            Set<VenuesDTO> venuesDTOSet = new HashSet<>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject resultObject = resultArray.getJSONObject(i);
                venuesDTOSet.add(addVenueDTO(resultObject));
            }
            responseDTO.setStatus(HttpStatus.OK);
            responseDTO.setMessage(AppConstants.LOCATION_POPULATED);
            responseDTO.setLocations(venuesDTOSet);
            return responseDTO;
        }catch(HttpClientErrorException e) {
            log.error("Error while searching for location", e);
            responseDTO.setStatus(HttpStatus.UNAUTHORIZED);
            responseDTO.setMessage(AppConstants.GOOGLE_KEY_MISSING);
            return responseDTO;
        }
    }

    private VenuesDTO addVenueDTO(JSONObject resultObject) {
        VenuesDTO venuesDTO = new VenuesDTO();
        JSONArray addressComponentsJson = resultObject.getJSONArray("address_components");
        for (int j = 0; j < addressComponentsJson.length(); j++) {
            JSONObject jsonObject = addressComponentsJson.getJSONObject(j);
            venuesDTO.setName(addressComponentsJson.getJSONObject(0).getString(AppConstants.LONG_NAME));
            if(jsonObject.getJSONArray(AppConstants.TYPES).toString().contains("administrative_area_level_2") ||
                    jsonObject.getJSONArray(AppConstants.TYPES).toString().contains("locality"))
                venuesDTO.setCity(jsonObject.getString(AppConstants.LONG_NAME));
            if(jsonObject.getJSONArray(AppConstants.TYPES).toString().contains("administrative_area_level_1"))
                venuesDTO.setState(jsonObject.getString(AppConstants.LONG_NAME));
            if(jsonObject.getJSONArray(AppConstants.TYPES).toString().contains("country")) {
                venuesDTO.setCountry(jsonObject.getString(AppConstants.LONG_NAME));
                venuesDTO.setCountryCode(jsonObject.getString("short_name"));
            }
            if (jsonObject.getJSONArray(AppConstants.TYPES).toString().contains("postal_code"))
                venuesDTO.setPostalCode(jsonObject.getString(AppConstants.LONG_NAME));
            venuesDTO.setAddress(resultObject.getString("formatted_address"));
            JSONObject locationObject = resultObject.getJSONObject("geometry").getJSONObject("location");
            venuesDTO.setLat(locationObject.get("lat").toString());
            venuesDTO.setLng(locationObject.get("lng").toString());
            String categoryList = "";
            for (int k = 0; k < resultObject.getJSONArray(AppConstants.TYPES).length(); k++) {
                String category = resultObject.getJSONArray(AppConstants.TYPES).getString(k);
                categoryList = categoryList.concat(category);
                if (k < resultObject.getJSONArray(AppConstants.TYPES).length() - 1)
                    categoryList = categoryList.concat(", ");
            }
            venuesDTO.setCategory(categoryList);
        }
        return venuesDTO;
    }

    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    public String getGoogleUrl() {
        String apiPath = null;
        String googleKey = null;
        if (applicationProperties.getGoogleGeocode() != null) {
            apiPath = applicationProperties.getGoogleGeocode().getApiPath();
            googleKey = applicationProperties.getGoogleGeocode().getGoogleKey();
        }
        return apiPath +
                "?key=" + googleKey;
    }
}
