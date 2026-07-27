package com.agroflowers.sales_service.dto;

public record CostSummaryDto(
        Long shipmentId,
        String shipmentNumber,
        double flowerCost,
        double airFreight,
        double packing,
        double labels,
        double taxes,
        double groundTransport,
        double insurance,
        double handling,
        double otherCosts,
        double totalCost
) {
}
