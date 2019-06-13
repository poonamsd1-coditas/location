package com.location.location.service;

import com.location.location.config.ApplicationProperties;
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
import java.util.Arrays;
import java.util.Date;

@Service
public class FourSquareService {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RestTemplate restTemplate;

    public String getLocation(String query) {
        String url = getFourSquareUrl();
        if (!query.isEmpty())
            url = url.concat("&near=" + query);

        String responseJson = getResponseJson(url);

        JSONObject jsonObj = new JSONObject(responseJson);
        JSONObject jsonObject = (JSONObject) jsonObj.get("response");
        JSONArray jsonArrayVenues = (JSONArray) jsonObject.get("venues");

        return jsonArrayVenues.getJSONObject(0).getString("name");
    }

    private String getResponseJson(String url) {
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
