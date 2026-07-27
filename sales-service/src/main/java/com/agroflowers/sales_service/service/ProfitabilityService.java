package com.agroflowers.sales_service.service;

import java.time.LocalDate;
import java.util.List;

import com.agroflowers.sales_service.dto.ProfitabilityResponseDto;
import com.agroflowers.sales_service.model.ProfitabilityClassification;

public interface ProfitabilityService {

    List<ProfitabilityResponseDto> listProfitability(
            LocalDate dateFrom,
            LocalDate dateTo,
            String farmName,
            String customer,
            String shipmentNumber,
            ProfitabilityClassification classification,
            Double minMargin,
            Double maxMargin,
            String bearerToken
    );
}
