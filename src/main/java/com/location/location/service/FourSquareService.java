package com.location.location.service;

import com.location.location.config.ApplicationProperties;
import com.location.location.config.ErrorCodes;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.sun.org.omg.CORBA.ExceptionDescriptionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FourSquareService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public CustomResponseDTO getLocation(String query, String filter) {

        String url = null;
        CustomResponseDTO responseDTO = new CustomResponseDTO();
        url = getFourSquareUrl().concat("&near=" + query);

        boolean filterApplied = false;
        if (filter != null && !filter.isEmpty())
            filterApplied = true;
        String dataUrl = null;
        try {
            dataUrl = getDataJson(url);
        }catch (HttpClientErrorException e) {
            responseDTO.setStatus(e.getStatusCode());
            return getResponse(responseDTO);
        }

        JSONObject dataJson = new JSONObject(dataUrl);

        JSONObject responseJson = (JSONObject) dataJson.get("response");
        JSONArray venuesArray = (JSONArray) responseJson.get("venues");
        List<VenuesDTO> venuesDTOList = new ArrayList<>();
        for (int i = 0; i < venuesArray.length(); i++) {
            JSONObject venueObject = venuesArray.getJSONObject(i);
            VenuesDTO venuesDTO = new VenuesDTO();
            venuesDTO.setName(venueObject.getString("name"));
            JSONObject locationObject = venueObject.getJSONObject("location");
            if (locationObject.has("city"))
                venuesDTO.setCity(locationObject.getString("city"));
            if (locationObject.has("state"))
                venuesDTO.setState(locationObject.getString("state"));
            if (locationObject.has("country"))
                venuesDTO.setCountry(locationObject.getString("country"));
            if (locationObject.has("cc"))
                venuesDTO.setCountryCode(locationObject.getString("cc"));
            if (locationObject.has("postalCode"))
                venuesDTO.setPostalCode(locationObject.getString("postalCode"));
            String address = "";
            for(int j = 0; j < locationObject.getJSONArray("formattedAddress").length(); j++)
                address = address.concat(locationObject.getJSONArray("formattedAddress").getString(j) + " ");
            venuesDTO.setAddress(address);
            if (locationObject.has("labeledLatLngs")) {
                venuesDTO.setLat(locationObject.getJSONArray("labeledLatLngs").getJSONObject(0).get("lat").toString());
                venuesDTO.setLng(locationObject.getJSONArray("labeledLatLngs").getJSONObject(0).get("lng").toString());
            }
            String categoryList = "";
            boolean notFound = true;
            for (int j = 0; j < venueObject.getJSONArray("categories").length(); j++) {
                String category = venueObject.getJSONArray("categories").getJSONObject(j).getString("name");
                categoryList = categoryList.concat(category);
                if (j < venueObject.getJSONArray("categories").length() - 1)
                    categoryList = categoryList.concat(", ");
                if (filterApplied && category.toLowerCase().contains(filter.toLowerCase()))
                    notFound = false;
            }
            if (filterApplied && notFound)
                continue;
            venuesDTO.setCategory(categoryList);
            venuesDTOList.add(venuesDTO);
        }
        responseDTO.setStatus(HttpStatus.OK);
        responseDTO.setLocations(venuesDTOList);
        return getResponse(responseDTO);
    }

    public CustomResponseDTO getResponse(CustomResponseDTO responseDTO) {
        if (responseDTO.getStatus().equals(HttpStatus.OK))
            responseDTO.setMessage(ErrorCodes.LOCATION_POPULATED);
        else if (responseDTO.getStatus().equals(HttpStatus.BAD_REQUEST))
            responseDTO.setMessage(ErrorCodes.FAILED_GEOCODE);
        else if (responseDTO.getStatus().equals(HttpStatus.UNAUTHORIZED))
            responseDTO.setMessage(ErrorCodes.INVALID_AUTH);
        return responseDTO;
    }

    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    private String getFourSquareUrl() {
        String apiPath = null, clientSecret = null, clientId = null;
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
