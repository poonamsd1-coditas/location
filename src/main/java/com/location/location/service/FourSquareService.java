package com.location.location.service;

import com.location.location.config.ApplicationProperties;
import com.location.location.dto.VenuesDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    public List<VenuesDTO> getLocation(String query, String filter) {
        String url = getFourSquareUrl();
        if (!query.isEmpty())
            url = url.concat("&near=" + query);

        boolean filterApplied = false;
        if (filter != null && !filter.isEmpty())
            filterApplied = true;
        JSONObject dataJson = new JSONObject(getDataJson(url));
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
                categoryList = categoryList.concat(category + " ");
                if (j < venueObject.getJSONArray("categories").length() - 1)
                    categoryList = categoryList.concat(",");
                if (filterApplied && category.toLowerCase().contains(filter.toLowerCase()))
                    notFound = false;
            }
            if (filterApplied && notFound)
                continue;
            venuesDTO.setCategory(categoryList);
            venuesDTOList.add(venuesDTO);
        }
        return venuesDTOList;
    }

    private String getDataJson(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    private String getFourSquareUrl() {
        return applicationProperties.getFourSquare().getApiPath() +
                "?client_id=" + applicationProperties.getFourSquare().getClientId() +
                "&client_secret=" + applicationProperties.getFourSquare().getClientSecret() +
                "&intent=browse&v=" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

}
