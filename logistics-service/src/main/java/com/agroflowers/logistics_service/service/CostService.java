package com.agroflowers.logistics_service.service;

import java.util.List;

import com.agroflowers.logistics_service.dto.CostRequestDto;
import com.agroflowers.logistics_service.dto.CostResponseDto;
import com.agroflowers.logistics_service.dto.PagedResponseDto;

public interface CostService {

    PagedResponseDto<CostResponseDto> listCosts(int page, int pageSize, String search);

    CostResponseDto getCostByShipmentId(Long shipmentId);

    CostResponseDto createCost(CostRequestDto request);

    List<CostResponseDto> listAllCosts();
}
