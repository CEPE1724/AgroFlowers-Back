package com.agroflowers.sales_service.dto;

import java.time.LocalDate;

import com.agroflowers.sales_service.model.ProfitabilityClassification;

public record ProfitabilityResponseDto(
        Long shipmentId,
        String shipmentNumber,
        LocalDate shipmentDate,
        String farmName,
        String customer,
        Double totalSale,
        Double totalCost,
        Double profit,
        Double profitMargin,
        ProfitabilityClassification classification
) {
}
