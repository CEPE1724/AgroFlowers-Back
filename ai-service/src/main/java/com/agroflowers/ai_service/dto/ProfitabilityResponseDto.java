package com.agroflowers.ai_service.dto;

import java.time.LocalDate;

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
        String classification
) {
}
