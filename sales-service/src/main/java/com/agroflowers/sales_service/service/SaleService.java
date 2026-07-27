package com.agroflowers.sales_service.service;

import com.agroflowers.sales_service.dto.PagedResponseDto;
import com.agroflowers.sales_service.dto.SaleRequestDto;
import com.agroflowers.sales_service.dto.SaleResponseDto;

public interface SaleService {

    PagedResponseDto<SaleResponseDto> listSales(int page, int pageSize, String search);

    SaleResponseDto getSaleById(Long id);

    SaleResponseDto createSale(SaleRequestDto request, String bearerToken);
}
