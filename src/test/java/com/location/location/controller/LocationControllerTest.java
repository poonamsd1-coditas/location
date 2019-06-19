package com.location.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.location.location.LocationApplication;
import com.location.location.config.AppConstants;
import com.location.location.config.ApplicationProperties;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.dto.VenuesDTO;
import com.location.location.service.LocationService;
import com.location.location.service.impl.FourSquareService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        assertThat(responseDTO.getMessage().equals(AppConstants.LOCATION_POPULATED)).isTrue();
        assertThat(!responseDTO.getLocations().isEmpty()).isTrue();
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
        assertThat(responseDTO.getMessage().equals(AppConstants.LOCATION_POPULATED)).isTrue();
        assertThat(!responseDTO.getLocations().isEmpty()).isTrue();
        for (VenuesDTO venuesDTO : responseDTO.getLocations()) {
            assertThat(venuesDTO.getCategory().toLowerCase().contains("bank")).isTrue();
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
        assertThat(responseDTO.getMessage().equals(AppConstants.INVALID_QUERY)).isTrue();
        assertThat(responseDTO.getLocations() == null).isTrue();
    }
}
