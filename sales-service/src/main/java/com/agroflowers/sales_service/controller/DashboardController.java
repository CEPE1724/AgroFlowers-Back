package com.agroflowers.sales_service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.sales_service.dto.DashboardChartsDto;
import com.agroflowers.sales_service.dto.DashboardSummaryDto;
import com.agroflowers.sales_service.dto.RecentShipmentDto;
import com.agroflowers.sales_service.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return ResponseEntity.ok(dashboardService.getSummary(bearerToken));
    }

    @GetMapping("/charts")
    public ResponseEntity<DashboardChartsDto> getCharts(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return ResponseEntity.ok(dashboardService.getCharts(bearerToken));
    }

    @GetMapping("/recent-shipments")
    public ResponseEntity<List<RecentShipmentDto>> getRecentShipments(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        return ResponseEntity.ok(dashboardService.getRecentShipments(bearerToken));
    }
}
