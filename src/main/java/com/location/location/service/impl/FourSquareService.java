package com.location.location.service.impl;

import com.location.location.config.ApplicationProperties;
import com.location.location.config.AppConstants;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.GeoLocationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class FourSquareService implements GeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(FourSquareService.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public CustomResponseDTO getLocation(String query, String filter) {
        log.info("Request to get places for a location:{} with filters: {}", query, filter);
        CustomResponseDTO responseDTO = new CustomResponseDTO();
        String url = getFourSquareUrl().concat("&near=" + query);
        boolean filterApplied = !StringUtils.isEmpty(filter);
        String dataUrl;
        try {
            dataUrl = getDataJson(url);
            JSONObject dataJson = new JSONObject(dataUrl);
            JSONObject responseJson = (JSONObject) dataJson.get("response");
            JSONArray venuesArray = (JSONArray) responseJson.get("venues");
            Set<VenuesDTO> venuesDTOSet = new HashSet<>();
            for (int i = 0; i < venuesArray.length(); i++) {
                VenuesDTO venuesDTO = addVenueDTO(venuesArray.getJSONObject(i), filterApplied, filter);
                if (venuesDTO != null)
                    venuesDTOSet.add(venuesDTO);
            }
            responseDTO.setStatus(HttpStatus.OK);
            responseDTO.setLocations(venuesDTOSet);
            return getResponse(responseDTO);
        }catch (HttpClientErrorException e) {
            log.error("Error while searching for location", e);
            responseDTO.setStatus(e.getStatusCode());
            return getResponse(responseDTO);
        }
    }

    private VenuesDTO addVenueDTO(JSONObject venueObject, boolean filterApplied, String filter) {
        VenuesDTO venuesDTO = new VenuesDTO();
        try {
            venuesDTO.setName(venueObject.getString("name"));
            JSONObject locationObject = venueObject.getJSONObject("location");
            venuesDTO.setCity(locationObject.getString("city"));
            venuesDTO.setState(locationObject.getString("state"));
            venuesDTO.setCountry(locationObject.getString("country"));
            venuesDTO.setCountryCode(locationObject.getString("cc"));
            venuesDTO.setPostalCode(locationObject.getString("postalCode"));
            String address = "";
            for (int j = 0; j < locationObject.getJSONArray("formattedAddress").length(); j++)
                address = address.concat(locationObject.getJSONArray("formattedAddress").getString(j) + " ");
            venuesDTO.setAddress(address);
            if (locationObject.has(AppConstants.LAT_LNG)) {
                venuesDTO.setLat(locationObject.getJSONArray(AppConstants.LAT_LNG).getJSONObject(0).get("lat").toString());
                venuesDTO.setLng(locationObject.getJSONArray(AppConstants.LAT_LNG).getJSONObject(0).get("lng").toString());
            }
            String categoryList = "";
            boolean notFound = true;
            for (int j = 0; j < venueObject.getJSONArray(AppConstants.CATEGORIES).length(); j++) {
                String category = venueObject.getJSONArray(AppConstants.CATEGORIES).getJSONObject(j).getString("name");
                categoryList = categoryList.concat(category);
                if (j < venueObject.getJSONArray(AppConstants.CATEGORIES).length() - 1)
                    categoryList = categoryList.concat(", ");
                if (filterApplied && category.toLowerCase().contains(filter.toLowerCase()))
                    notFound = false;
            }
            if (filterApplied && notFound)
                return null;
            venuesDTO.setCategory(categoryList);
        } catch (JSONException e) {
            log.error("Property not found");
            return null;
        }
        return venuesDTO;
    }

    private CustomResponseDTO getResponse(CustomResponseDTO responseDTO) {
        if (responseDTO.getStatus().equals(HttpStatus.OK))
            responseDTO.setMessage(AppConstants.LOCATION_POPULATED);
        else if (responseDTO.getStatus().equals(HttpStatus.BAD_REQUEST))
            responseDTO.setMessage(AppConstants.FAILED_GEOCODE);
        else if (responseDTO.getStatus().equals(HttpStatus.UNAUTHORIZED))
            responseDTO.setMessage(AppConstants.INVALID_AUTH);
        return responseDTO;
    }

    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

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
