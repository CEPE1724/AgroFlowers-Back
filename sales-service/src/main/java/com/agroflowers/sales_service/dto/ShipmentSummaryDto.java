package com.agroflowers.sales_service.dto;

import java.time.LocalDate;

public record ShipmentSummaryDto(
        Long id,
        String shipmentNumber,
        String farmName,
        LocalDate shipmentDate,
        String destination,
        String customer,
        String status
) {
}
