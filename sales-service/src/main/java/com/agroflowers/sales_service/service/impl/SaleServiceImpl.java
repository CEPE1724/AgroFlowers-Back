package com.agroflowers.sales_service.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.sales_service.client.LogisticsClient;
import com.agroflowers.sales_service.dto.PagedResponseDto;
import com.agroflowers.sales_service.dto.SaleDetailRequestDto;
import com.agroflowers.sales_service.dto.SaleDetailResponseDto;
import com.agroflowers.sales_service.dto.SaleRequestDto;
import com.agroflowers.sales_service.dto.SaleResponseDto;
import com.agroflowers.sales_service.dto.ShipmentSummaryDto;
import com.agroflowers.sales_service.model.Sale;
import com.agroflowers.sales_service.model.SaleDetail;
import com.agroflowers.sales_service.repository.SaleRepository;
import com.agroflowers.sales_service.service.SaleService;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final LogisticsClient logisticsClient;

    public SaleServiceImpl(SaleRepository saleRepository, LogisticsClient logisticsClient) {
        this.saleRepository = saleRepository;
        this.logisticsClient = logisticsClient;
    }

    @Override
    public PagedResponseDto<SaleResponseDto> listSales(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Sale> result = StringUtils.hasText(search)
                ? saleRepository.findBySaleNumberContainingIgnoreCaseOrCustomerContainingIgnoreCaseOrShipmentNumberContainingIgnoreCase(
                        search, search, search, pageable)
                : saleRepository.findAll(pageable);

        List<SaleResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    public SaleResponseDto getSaleById(Long id) {
        return toResponseDto(findSaleOrThrow(id));
    }

    @Override
    public SaleResponseDto createSale(SaleRequestDto request, String bearerToken) {
        ShipmentSummaryDto shipment = logisticsClient.getShipmentById(request.shipmentId(), bearerToken);

        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber());
        sale.setShipmentId(request.shipmentId());
        sale.setShipmentNumber(shipment.shipmentNumber());
        sale.setCustomer(request.customer());
        sale.setSaleDate(request.saleDate());
        sale.setCurrency(request.currency());
        sale.setPaymentStatus(request.paymentStatus());
        sale.setObservation(request.observation());

        double total = 0.0;

        for (SaleDetailRequestDto detailRequest : request.details()) {
            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProductId(detailRequest.productId());
            detail.setProductName(detailRequest.productName());
            detail.setQuantity(detailRequest.quantity());
            detail.setUnitPrice(detailRequest.unitPrice());

            double subtotal = round2(detailRequest.quantity() * detailRequest.unitPrice());
            detail.setSubtotal(subtotal);

            total += subtotal;
            sale.getDetails().add(detail);
        }

        sale.setTotalSale(round2(total));

        return toResponseDto(saleRepository.save(sale));
    }

    private String generateSaleNumber() {
        long nextSequence = saleRepository.count() + 1;
        return "VTA-" + String.format("%06d", nextSequence);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Sale findSaleOrThrow(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro la venta con id: " + id));
    }

    private SaleResponseDto toResponseDto(Sale sale) {
        List<SaleDetailResponseDto> details = sale.getDetails().stream()
                .map(detail -> new SaleDetailResponseDto(
                        detail.getProductId(),
                        detail.getProductName(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getSubtotal()
                ))
                .toList();

        return new SaleResponseDto(
                sale.getId(),
                sale.getSaleNumber(),
                sale.getShipmentId(),
                sale.getShipmentNumber(),
                sale.getCustomer(),
                sale.getSaleDate(),
                sale.getCurrency(),
                sale.getPaymentStatus(),
                sale.getObservation(),
                sale.getTotalSale(),
                details
        );
    }
}
