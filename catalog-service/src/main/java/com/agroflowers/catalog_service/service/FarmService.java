package com.agroflowers.catalog_service.service;

import java.util.List;

import com.agroflowers.catalog_service.dto.FarmRequestDto;
import com.agroflowers.catalog_service.dto.FarmResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;

public interface FarmService {

    PagedResponseDto<FarmResponseDto> listFarms(int page, int pageSize, String search);

    FarmResponseDto getFarmById(Long id);

    FarmResponseDto createFarm(FarmRequestDto request);

    FarmResponseDto updateFarm(Long id, FarmRequestDto request);

    void deactivateFarm(Long id);

    List<FarmResponseDto> listAllFarms();
}
