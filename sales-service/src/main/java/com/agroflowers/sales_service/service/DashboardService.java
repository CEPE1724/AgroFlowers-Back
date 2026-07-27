package com.agroflowers.sales_service.service;

import java.util.List;

import com.agroflowers.sales_service.dto.DashboardChartsDto;
import com.agroflowers.sales_service.dto.DashboardSummaryDto;
import com.agroflowers.sales_service.dto.RecentShipmentDto;

public interface DashboardService {

    DashboardSummaryDto getSummary(String bearerToken);

    DashboardChartsDto getCharts(String bearerToken);

    List<RecentShipmentDto> getRecentShipments(String bearerToken);
}
