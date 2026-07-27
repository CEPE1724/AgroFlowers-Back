package com.agroflowers.sales_service.dto;

import java.util.List;

public record DashboardChartsDto(
        List<FarmProfitabilityPointDto> farmProfitability,
        List<CostByCategoryPointDto> costsByCategory,
        List<SalesByVarietyPointDto> salesByVariety,
        List<MonthlyTrendPointDto> monthlyTrend,
        List<ShipmentsByStatusPointDto> shipmentsByStatus
) {

    public record FarmProfitabilityPointDto(String farmName, double profitMargin) {
    }

    public record CostByCategoryPointDto(String category, double amount) {
    }

    public record SalesByVarietyPointDto(String variety, double totalSold) {
    }

    public record MonthlyTrendPointDto(String month, double sales, double costs) {
    }

    public record ShipmentsByStatusPointDto(String status, long count) {
    }
}
