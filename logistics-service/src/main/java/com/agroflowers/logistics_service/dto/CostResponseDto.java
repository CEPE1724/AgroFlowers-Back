package com.agroflowers.logistics_service.dto;

public record CostResponseDto(
        Long id,
        Long shipmentId,
        String shipmentNumber,
        Double flowerCost,
        Double airFreight,
        Integer boxes,
        Double costPerBox,
        Double packing,
        Double costPerLabel,
        Double labels,
        Double taxBase,
        Double taxPercentage,
        Double taxes,
        Double groundTransport,
        Double insurance,
        Double handling,
        Double otherCosts,
        String otherCostsDescription,
        Double totalCost
) {
}
