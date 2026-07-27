package com.agroflowers.sales_service.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.agroflowers.sales_service.client.LogisticsClient;
import com.agroflowers.sales_service.dto.CostSummaryDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto.CostByCategoryPointDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto.FarmProfitabilityPointDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto.MonthlyTrendPointDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto.SalesByVarietyPointDto;
import com.agroflowers.sales_service.dto.DashboardChartsDto.ShipmentsByStatusPointDto;
import com.agroflowers.sales_service.dto.DashboardSummaryDto;
import com.agroflowers.sales_service.dto.RecentShipmentDto;
import com.agroflowers.sales_service.dto.ShipmentSummaryDto;
import com.agroflowers.sales_service.model.Sale;
import com.agroflowers.sales_service.model.SaleDetail;
import com.agroflowers.sales_service.repository.SaleRepository;
import com.agroflowers.sales_service.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final SaleRepository saleRepository;
    private final LogisticsClient logisticsClient;

    public DashboardServiceImpl(SaleRepository saleRepository, LogisticsClient logisticsClient) {
        this.saleRepository = saleRepository;
        this.logisticsClient = logisticsClient;
    }

    @Override
    public DashboardSummaryDto getSummary(String bearerToken) {
        YearMonth currentMonth = YearMonth.now();

        List<Sale> sales = saleRepository.findAll();
        List<ShipmentSummaryDto> shipments = logisticsClient.getAllShipments(bearerToken);
        List<CostSummaryDto> costs = logisticsClient.getAllCosts(bearerToken);

        Map<Long, LocalDate> shipmentDatesById = new LinkedHashMap<>();
        for (ShipmentSummaryDto shipment : shipments) {
            shipmentDatesById.put(shipment.id(), shipment.shipmentDate());
        }

        Map<Long, Double> costByShipmentId = new LinkedHashMap<>();
        for (CostSummaryDto cost : costs) {
            costByShipmentId.put(cost.shipmentId(), cost.totalCost());
        }

        long shipmentsThisMonth = shipments.stream()
                .filter(shipment -> YearMonth.from(shipment.shipmentDate()).equals(currentMonth))
                .count();

        List<Sale> salesThisMonthList = sales.stream()
                .filter(sale -> YearMonth.from(sale.getSaleDate()).equals(currentMonth))
                .toList();

        double salesThisMonth = round2(salesThisMonthList.stream().mapToDouble(Sale::getTotalSale).sum());

        double totalCostThisMonth = round2(salesThisMonthList.stream()
                .mapToDouble(sale -> costByShipmentId.getOrDefault(sale.getShipmentId(), 0.0))
                .sum());

        double totalProfit = round2(salesThisMonth - totalCostThisMonth);

        double averageMargin = salesThisMonthList.isEmpty() ? 0.0 : round2(
                salesThisMonthList.stream()
                        .mapToDouble(sale -> {
                            double cost = costByShipmentId.getOrDefault(sale.getShipmentId(), 0.0);
                            double profit = sale.getTotalSale() - cost;
                            return sale.getTotalSale() == 0 ? 0.0 : (profit / sale.getTotalSale()) * 100;
                        })
                        .average()
                        .orElse(0.0)
        );

        long lowMarginShipments = salesThisMonthList.stream()
                .filter(sale -> {
                    double cost = costByShipmentId.getOrDefault(sale.getShipmentId(), 0.0);
                    double profit = sale.getTotalSale() - cost;
                    double margin = sale.getTotalSale() == 0 ? 0.0 : (profit / sale.getTotalSale()) * 100;
                    return margin < 10;
                })
                .count();

        String bestSellingVariety = sales.stream()
                .flatMap(sale -> sale.getDetails().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        SaleDetail::getProductName,
                        java.util.stream.Collectors.summingDouble(SaleDetail::getSubtotal)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Map<Long, String> farmNameByShipmentId = new LinkedHashMap<>();
        for (ShipmentSummaryDto shipment : shipments) {
            farmNameByShipmentId.put(shipment.id(), shipment.farmName());
        }

        String mostProfitableFarm = sales.stream()
                .filter(sale -> farmNameByShipmentId.containsKey(sale.getShipmentId()))
                .collect(java.util.stream.Collectors.groupingBy(
                        sale -> farmNameByShipmentId.get(sale.getShipmentId()),
                        java.util.stream.Collectors.summingDouble(sale ->
                                sale.getTotalSale() - costByShipmentId.getOrDefault(sale.getShipmentId(), 0.0))
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new DashboardSummaryDto(
                shipmentsThisMonth,
                salesThisMonth,
                totalCostThisMonth,
                totalProfit,
                averageMargin,
                mostProfitableFarm,
                bestSellingVariety,
                lowMarginShipments
        );
    }

    @Override
    public DashboardChartsDto getCharts(String bearerToken) {
        List<Sale> sales = saleRepository.findAll();
        List<ShipmentSummaryDto> shipments = logisticsClient.getAllShipments(bearerToken);
        List<CostSummaryDto> costs = logisticsClient.getAllCosts(bearerToken);

        Map<Long, Double> costByShipmentIdForCharts = new LinkedHashMap<>();
        for (CostSummaryDto cost : costs) {
            costByShipmentIdForCharts.put(cost.shipmentId(), cost.totalCost());
        }

        Map<Long, String> farmNameByShipmentIdForCharts = new LinkedHashMap<>();
        for (ShipmentSummaryDto shipment : shipments) {
            farmNameByShipmentIdForCharts.put(shipment.id(), shipment.farmName());
        }

        List<FarmProfitabilityPointDto> farmProfitability = sales.stream()
                .filter(sale -> farmNameByShipmentIdForCharts.containsKey(sale.getShipmentId()))
                .collect(java.util.stream.Collectors.groupingBy(
                        sale -> farmNameByShipmentIdForCharts.get(sale.getShipmentId()),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.averagingDouble(sale -> {
                            double cost = costByShipmentIdForCharts.getOrDefault(sale.getShipmentId(), 0.0);
                            double profit = sale.getTotalSale() - cost;
                            return sale.getTotalSale() == 0 ? 0.0 : (profit / sale.getTotalSale()) * 100;
                        })
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(entry -> new FarmProfitabilityPointDto(entry.getKey(), round2(entry.getValue())))
                .toList();

        List<CostByCategoryPointDto> costsByCategory = List.of(
                new CostByCategoryPointDto("Flor", round2(costs.stream().mapToDouble(CostSummaryDto::flowerCost).sum())),
                new CostByCategoryPointDto("Flete aereo", round2(costs.stream().mapToDouble(CostSummaryDto::airFreight).sum())),
                new CostByCategoryPointDto("Empaque y etiquetas", round2(costs.stream().mapToDouble(c -> c.packing() + c.labels()).sum())),
                new CostByCategoryPointDto("Impuestos", round2(costs.stream().mapToDouble(CostSummaryDto::taxes).sum())),
                new CostByCategoryPointDto("Transporte terrestre", round2(costs.stream().mapToDouble(CostSummaryDto::groundTransport).sum())),
                new CostByCategoryPointDto("Seguro", round2(costs.stream().mapToDouble(CostSummaryDto::insurance).sum())),
                new CostByCategoryPointDto("Manejo de carga", round2(costs.stream().mapToDouble(CostSummaryDto::handling).sum())),
                new CostByCategoryPointDto("Otros", round2(costs.stream().mapToDouble(CostSummaryDto::otherCosts).sum()))
        );

        Map<String, Double> salesByVarietyMap = sales.stream()
                .flatMap(sale -> sale.getDetails().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        SaleDetail::getProductName,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.summingDouble(SaleDetail::getSubtotal)
                ));

        List<SalesByVarietyPointDto> salesByVariety = salesByVarietyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(entry -> new SalesByVarietyPointDto(entry.getKey(), round2(entry.getValue())))
                .toList();

        Map<Long, Double> costByShipmentId = new LinkedHashMap<>();
        Map<Long, LocalDate> shipmentDatesById = new LinkedHashMap<>();
        for (CostSummaryDto cost : costs) {
            costByShipmentId.put(cost.shipmentId(), cost.totalCost());
        }
        for (ShipmentSummaryDto shipment : shipments) {
            shipmentDatesById.put(shipment.id(), shipment.shipmentDate());
        }

        List<MonthlyTrendPointDto> monthlyTrend = new ArrayList<>();
        YearMonth cursor = YearMonth.now().minusMonths(5);
        for (int i = 0; i < 6; i++) {
            YearMonth month = cursor.plusMonths(i);

            double salesTotal = round2(sales.stream()
                    .filter(sale -> YearMonth.from(sale.getSaleDate()).equals(month))
                    .mapToDouble(Sale::getTotalSale)
                    .sum());

            double costsTotal = round2(sales.stream()
                    .filter(sale -> YearMonth.from(sale.getSaleDate()).equals(month))
                    .mapToDouble(sale -> costByShipmentId.getOrDefault(sale.getShipmentId(), 0.0))
                    .sum());

            String label = month.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es"));
            monthlyTrend.add(new MonthlyTrendPointDto(label, salesTotal, costsTotal));
        }

        List<ShipmentsByStatusPointDto> shipmentsByStatus = shipments.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ShipmentSummaryDto::status,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new ShipmentsByStatusPointDto(entry.getKey(), entry.getValue()))
                .toList();

        return new DashboardChartsDto(farmProfitability, costsByCategory, salesByVariety, monthlyTrend, shipmentsByStatus);
    }

    @Override
    public List<RecentShipmentDto> getRecentShipments(String bearerToken) {
        return logisticsClient.getAllShipments(bearerToken).stream()
                .sorted(Comparator.comparing(ShipmentSummaryDto::shipmentDate).reversed())
                .limit(5)
                .map(shipment -> new RecentShipmentDto(
                        shipment.id(),
                        shipment.shipmentNumber(),
                        shipment.shipmentDate(),
                        shipment.destination(),
                        shipment.customer(),
                        shipment.status()
                ))
                .toList();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
