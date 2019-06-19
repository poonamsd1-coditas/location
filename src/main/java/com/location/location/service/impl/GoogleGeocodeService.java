package com.location.location.service.impl;

import com.location.location.config.ApplicationProperties;
import com.location.location.config.ErrorCodes;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.GeoLocationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GoogleGeocodeService implements GeoLocationService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public CustomResponseDTO getLocation(String query, String filter) {
        if (filter != null && !filter.isEmpty())
            query = query.concat(" " + filter);
        String url = getGoogleUrl();
        if (!query.isEmpty())
            url = url.concat("&address=" + query);

        CustomResponseDTO responseDTO = new CustomResponseDTO();
        String dataUrl = "";
        dataUrl = getDataJson(url);
        if (dataUrl.contains("error_message")) {
            responseDTO.setStatus(HttpStatus.UNAUTHORIZED);
            responseDTO.setMessage(ErrorCodes.GOOGLE_KEY_MISSING);
            return responseDTO;
        }
        JSONObject dataJson = new JSONObject(dataUrl);

        JSONArray resultArray = dataJson.getJSONArray("results");
        Set<VenuesDTO> venuesDTOSet = new HashSet<>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject resultObject = resultArray.getJSONObject(i);
            VenuesDTO venuesDTO = new VenuesDTO();
            JSONArray addressComponentsJson = resultObject.getJSONArray("address_components");
            for (int j = 0; j < addressComponentsJson.length(); j++) {
                JSONObject jsonObject = addressComponentsJson.getJSONObject(j);
                if (j == 0)
                    venuesDTO.setName(addressComponentsJson.getJSONObject(0).getString("long_name"));
                if(jsonObject.getJSONArray("types").toString().contains("administrative_area_level_2") ||
                        jsonObject.getJSONArray("types").toString().contains("locality"))
                    venuesDTO.setCity(jsonObject.getString("long_name"));
                if(jsonObject.getJSONArray("types").toString().contains("administrative_area_level_1"))
                    venuesDTO.setState(jsonObject.getString("long_name"));
                if(jsonObject.getJSONArray("types").toString().contains("country")) {
                    venuesDTO.setCountry(jsonObject.getString("long_name"));
                    venuesDTO.setCountryCode(jsonObject.getString("short_name"));
                }
                if (jsonObject.getJSONArray("types").toString().contains("postal_code"))
                    venuesDTO.setPostalCode(jsonObject.getString("long_name"));
                venuesDTO.setAddress(resultObject.getString("formatted_address"));
                JSONObject locationObject = resultObject.getJSONObject("geometry").getJSONObject("location");
                venuesDTO.setLat(locationObject.get("lat").toString());
                venuesDTO.setLng(locationObject.get("lng").toString());
                String categoryList = "";
                for (int k = 0; k < resultObject.getJSONArray("types").length(); k++) {
                    String category = resultObject.getJSONArray("types").getString(k);
                    categoryList = categoryList.concat(category);
                    if (k < resultObject.getJSONArray("types").length() - 1)
                        categoryList = categoryList.concat(", ");
                }
                venuesDTO.setCategory(categoryList);
            }
            venuesDTOSet.add(venuesDTO);
        }
        responseDTO.setStatus(HttpStatus.OK);
        responseDTO.setMessage(ErrorCodes.LOCATION_POPULATED);
        responseDTO.setLocations(venuesDTOSet);
        return responseDTO;
    }

    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    public String getGoogleUrl() {
        String apiPath = null, googleKey = null;
        if (applicationProperties.getGoogleGeocode() != null) {
            apiPath = applicationProperties.getGoogleGeocode().getApiPath();
            googleKey = applicationProperties.getGoogleGeocode().getGoogleKey();
        }
        return apiPath +
                "?key=" + googleKey;
    }
}
