package com.location.location.service;

import com.location.location.dto.CustomResponseDTO;

public interface GeoLocationService {

    /**
     * Get location based on query and filter
     * @param query -- search string
     * @param filter -- filter by category or type
     * @return CustomResponseDTO
     */
    CustomResponseDTO getLocation(String query, String filter);
}
