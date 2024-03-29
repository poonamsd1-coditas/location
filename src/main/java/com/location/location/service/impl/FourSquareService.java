package com.location.location.service.impl;

import com.location.location.config.AppConstants;
import com.location.location.config.ApplicationProperties;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.GeoLocationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Poonam Doddamani
 * FourSquare Service implements the FourSquare API to provide list of locations,
 * and filters the results by category/type
 */
@Service
public class FourSquareService implements GeoLocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FourSquareService.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get list of places by FourSquare API
     *
     * @param query -- search string
     * @param filter -- filter by category or type
     * @return List of VenuesDTO in CustomResponseDTO
     */
    public CustomResponseDTO getLocation(String query, String filter) {
        LOGGER.info("Request to FourSquare to get places for a location:{} with filters: {}", query, filter);
        CustomResponseDTO responseDTO = new CustomResponseDTO();
        String url = getFourSquareUrl().concat("&near=" + query);
        boolean filterApplied = !StringUtils.isEmpty(filter);
        try {
            String dataUrl = getDataJson(url);
            JSONObject dataJson = new JSONObject(dataUrl);
            JSONObject responseJson = (JSONObject) dataJson.get("response");
            JSONArray venuesArray = (JSONArray) responseJson.get("venues");
            String response = "";
            Set<VenuesDTO> venuesDTOSet = new HashSet<>();
            for (int i = 0; i < venuesArray.length(); i++) {
                response = addVenueDTO(venuesArray.getJSONObject(i), venuesDTOSet);
            }
            if (filterApplied && !CollectionUtils.isEmpty(venuesDTOSet)) {
                venuesDTOSet = venuesDTOSet.stream().filter(venuesDTO ->
                        venuesDTO.getCategory().toLowerCase().contains(filter.toLowerCase())
                ).collect(Collectors.toSet());
            }
            responseDTO.setLocations(venuesDTOSet);
            if (response.equals(AppConstants.PROPERTY_NOT_FOUND) && CollectionUtils.isEmpty(responseDTO.getLocations())) {
                responseDTO.setMessage(response);
                responseDTO.setStatus(HttpStatus.EXPECTATION_FAILED);
            } else {
                responseDTO.setMessage(AppConstants.LOCATION_POPULATED);
                responseDTO.setStatus(HttpStatus.OK);
            }
        }catch (HttpClientErrorException e) {
            LOGGER.error("Error while searching for location", e);
            if (e.getRawStatusCode() == 400) {
                responseDTO.setStatus(HttpStatus.BAD_REQUEST);
                responseDTO.setMessage(AppConstants.FAILED_GEOCODE);
            }
            else if (e.getRawStatusCode() == 401) {
                responseDTO.setStatus(HttpStatus.UNAUTHORIZED);
                responseDTO.setMessage(AppConstants.INVALID_AUTH);
            }
            else if (e.getRawStatusCode() == 500) {
                responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                responseDTO.setMessage(AppConstants.INTERNAL_ERROR);
            }
        }
        return responseDTO;
    }

    /**
     *
     * @param venueObject location object returned by FourSquare API
     * @param venuesDTOSet set of locations to be added to
     * @return response
     */
    private String addVenueDTO(JSONObject venueObject, Set<VenuesDTO> venuesDTOSet) {
        VenuesDTO venuesDTO = new VenuesDTO();
        String response = "";
        try {
            venuesDTO.setName(venueObject.getString("name"));
            JSONObject locationObject = venueObject.getJSONObject("location");
            venuesDTO.setCity(locationObject.getString("city"));
            venuesDTO.setState(locationObject.getString("state"));
            venuesDTO.setCountry(locationObject.getString("country"));
            venuesDTO.setCountryCode(locationObject.getString("cc"));
            venuesDTO.setPostalCode(locationObject.getString("postalCode"));
            String address = "";
            for (int j = 0; j < locationObject.getJSONArray("formattedAddress").length(); j++) {
                address = address.concat(locationObject.getJSONArray("formattedAddress").getString(j) + " ");
            }
            venuesDTO.setAddress(address);
            if (locationObject.has(AppConstants.LAT_LNG)) {
                venuesDTO.setLat(locationObject.getJSONArray(AppConstants.LAT_LNG).getJSONObject(0).get("lat").toString());
                venuesDTO.setLng(locationObject.getJSONArray(AppConstants.LAT_LNG).getJSONObject(0).get("lng").toString());
            }
            String categoryList = "";
            for (int j = 0; j < venueObject.getJSONArray(AppConstants.CATEGORIES).length(); j++) {
                String category = venueObject.getJSONArray(AppConstants.CATEGORIES).getJSONObject(j).getString("name");
                categoryList = categoryList.concat(category);
                if (j < venueObject.getJSONArray(AppConstants.CATEGORIES).length() - 1) {
                    categoryList = categoryList.concat(", ");
                }
            }
            venuesDTO.setCategory(categoryList);
            venuesDTOSet.add(venuesDTO);
        } catch (JSONException e) {
            LOGGER.error("Property not found", e);
            response = AppConstants.PROPERTY_NOT_FOUND;
        }
        return response;
    }

    /**
     * Get data from service provider API
     *
     * @param url -- API URL
     * @return location data
     */
    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    /**
     * Get API URL by fetching auth params from Application Properties
     * @return URL
     */
    private String getFourSquareUrl() {
        String apiPath = null;
        String clientId = null;
        String clientSecret = null;
        if (applicationProperties.getFourSquare() != null) {
            apiPath = applicationProperties.getFourSquare().getApiPath();
            clientId = applicationProperties.getFourSquare().getClientId();
            clientSecret = applicationProperties.getFourSquare().getClientSecret();
        }
        return apiPath +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&intent=browse&v=" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

}
