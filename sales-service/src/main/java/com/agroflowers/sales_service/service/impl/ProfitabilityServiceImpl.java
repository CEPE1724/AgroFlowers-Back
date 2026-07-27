package com.agroflowers.sales_service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.sales_service.client.LogisticsClient;
import com.agroflowers.sales_service.dto.CostSummaryDto;
import com.agroflowers.sales_service.dto.ProfitabilityResponseDto;
import com.agroflowers.sales_service.dto.ShipmentSummaryDto;
import com.agroflowers.sales_service.model.ProfitabilityClassification;
import com.agroflowers.sales_service.model.Sale;
import com.agroflowers.sales_service.repository.SaleRepository;
import com.agroflowers.sales_service.service.ProfitabilityService;

@Service
public class ProfitabilityServiceImpl implements ProfitabilityService {

    private final SaleRepository saleRepository;
    private final LogisticsClient logisticsClient;

    public ProfitabilityServiceImpl(SaleRepository saleRepository, LogisticsClient logisticsClient) {
        this.saleRepository = saleRepository;
        this.logisticsClient = logisticsClient;
    }

    @Override
    public List<ProfitabilityResponseDto> listProfitability(
            LocalDate dateFrom,
            LocalDate dateTo,
            String farmName,
            String customer,
            String shipmentNumber,
            ProfitabilityClassification classification,
            Double minMargin,
            Double maxMargin,
            String bearerToken
    ) {
        return saleRepository.findAll().stream()
                .map(sale -> toProfitabilityRecord(sale, bearerToken))
                .filter(record -> dateFrom == null || !record.shipmentDate().isBefore(dateFrom))
                .filter(record -> dateTo == null || !record.shipmentDate().isAfter(dateTo))
                .filter(record -> !StringUtils.hasText(farmName) || record.farmName().toLowerCase().contains(farmName.toLowerCase()))
                .filter(record -> !StringUtils.hasText(customer) || record.customer().toLowerCase().contains(customer.toLowerCase()))
                .filter(record -> !StringUtils.hasText(shipmentNumber) || record.shipmentNumber().toLowerCase().contains(shipmentNumber.toLowerCase()))
                .filter(record -> classification == null || record.classification() == classification)
                .filter(record -> minMargin == null || record.profitMargin() >= minMargin)
                .filter(record -> maxMargin == null || record.profitMargin() <= maxMargin)
                .toList();
    }

    private ProfitabilityResponseDto toProfitabilityRecord(Sale sale, String bearerToken) {
        ShipmentSummaryDto shipment = logisticsClient.getShipmentById(sale.getShipmentId(), bearerToken);
        Optional<CostSummaryDto> cost = logisticsClient.getCostByShipmentId(sale.getShipmentId(), bearerToken);

        double totalCost = cost.map(CostSummaryDto::totalCost).orElse(0.0);
        double profit = round2(sale.getTotalSale() - totalCost);
        double profitMargin = sale.getTotalSale() == 0 ? 0.0 : round2((profit / sale.getTotalSale()) * 100);

        return new ProfitabilityResponseDto(
                sale.getShipmentId(),
                sale.getShipmentNumber(),
                shipment.shipmentDate(),
                shipment.farmName(),
                sale.getCustomer(),
                sale.getTotalSale(),
                totalCost,
                profit,
                profitMargin,
                classify(profitMargin)
        );
    }

    private ProfitabilityClassification classify(double margin) {
        if (margin >= 20) return ProfitabilityClassification.EXCELLENT;
        if (margin >= 15) return ProfitabilityClassification.GOOD;
        if (margin >= 10) return ProfitabilityClassification.ACCEPTABLE;
        return ProfitabilityClassification.LOW;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
