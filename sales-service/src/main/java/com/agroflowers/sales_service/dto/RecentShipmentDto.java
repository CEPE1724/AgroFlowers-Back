package com.agroflowers.sales_service.dto;

import java.time.LocalDate;

public record RecentShipmentDto(
        Long id,
        String shipmentNumber,
        LocalDate shipmentDate,
        String destination,
        String customer,
        String status
) {
}
