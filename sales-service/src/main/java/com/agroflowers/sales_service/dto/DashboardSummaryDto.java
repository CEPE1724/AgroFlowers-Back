package com.agroflowers.sales_service.dto;

public record DashboardSummaryDto(
        long shipmentsThisMonth,
        double salesThisMonth,
        double totalCostThisMonth,
        double totalProfit,
        double averageMargin,
        String mostProfitableFarm,
        String bestSellingVariety,
        long lowMarginShipments
) {
}
