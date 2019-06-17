package com.location.location;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.location.location.controller.LocationController;
import com.location.location.dto.CustomResponseDTO;
import com.location.location.service.LocationService;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocationApplication.class)
public class LocationServiceTest {

    private MockMvc mockMvc;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationController locationController;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(locationController)
                .setMessageConverters(jacksonMessageConverter).build();;
    }

    @Test
    public void test_get_all_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/location/getLocation?query=pune"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        CustomResponseDTO responseDTO = mapper.readValue(content, CustomResponseDTO.class);
        assertThat(!responseDTO.getLocations().isEmpty());
    }
}
