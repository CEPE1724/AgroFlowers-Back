package com.agroflowers.sales_service.dto;

import java.time.LocalDate;
import java.util.List;

import com.agroflowers.sales_service.model.PaymentStatus;

public record SaleResponseDto(
        Long id,
        String saleNumber,
        Long shipmentId,
        String shipmentNumber,
        String customer,
        LocalDate saleDate,
        String currency,
        PaymentStatus paymentStatus,
        String observation,
        Double totalSale,
        List<SaleDetailResponseDto> details
) {
}
