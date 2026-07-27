package com.agroflowers.catalog_service.service;

import java.util.List;

import com.agroflowers.catalog_service.dto.FlowerRequestDto;
import com.agroflowers.catalog_service.dto.FlowerResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;

public interface FlowerService {

    PagedResponseDto<FlowerResponseDto> listFlowers(int page, int pageSize, String search);

    FlowerResponseDto getFlowerById(Long id);

    FlowerResponseDto createFlower(FlowerRequestDto request);

    FlowerResponseDto updateFlower(Long id, FlowerRequestDto request);

    List<FlowerResponseDto> listAllFlowers();
}
