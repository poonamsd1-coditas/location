package com.location.location.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.location.location.LocationApplication;
import com.location.location.config.ApplicationProperties;
import com.location.location.controller.LocationController;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.FourSquareService;
import com.location.location.service.LocationService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocationApplication.class)
public class LocationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    ApplicationProperties applicationProperties;

    @Mock
    RestTemplate restTemplate;

    @Autowired
    private LocationService locationService;

    @Autowired
    private FourSquareService fourSquareService;

    @Autowired
    private LocationController locationController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(locationController).build();
    }

    @Test
    public void test_status_ok() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/location/getLocation?query=pune"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CustomResponseDTO responseDTO = mapper.readValue(content, CustomResponseDTO.class);
        assertThat(responseDTO.getError() == null);
        assertThat(responseDTO.getMessage().equals("Locations populated"));
        assertThat(!responseDTO.getLocations().isEmpty());
    }

    @Test
    public void test_with_filter() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/location/getLocation?query=pune&filter=bank"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CustomResponseDTO responseDTO = mapper.readValue(content, CustomResponseDTO.class);
        assertThat(responseDTO.getError() == null);
        assertThat(responseDTO.getMessage().equals("Locations populated"));
        assertThat(!responseDTO.getLocations().isEmpty());
        for (VenuesDTO venuesDTO : responseDTO.getLocations()) {
            assertThat(venuesDTO.getCategory().toLowerCase().contains("bank"));
        }
    }

    @Test
    public void test_empty_query() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/location/getLocation?query="))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CustomResponseDTO responseDTO = mapper.readValue(content, CustomResponseDTO.class);
        assertThat(responseDTO.getError().equals("Query string is empty. Enter valid query"));
        assertThat(responseDTO.getLocations() == null);
    }
}
