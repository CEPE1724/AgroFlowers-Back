package com.agroflowers.logistics_service.dto;

import java.time.LocalDate;

import com.agroflowers.logistics_service.model.ShipmentStatus;

public record ShipmentResponseDto(
        Long id,
        String shipmentNumber,
        Long farmId,
        String farmName,
        LocalDate shipmentDate,
        String freightCompany,
        String airline,
        String airWaybill,
        String flightNumber,
        String origin,
        String destination,
        String customer,
        Integer boxes,
        Double actualWeight,
        Double volumetricWeight,
        Double billableWeight,
        Double ratePerKg,
        Double estimatedFreight,
        ShipmentStatus status
) {
}
