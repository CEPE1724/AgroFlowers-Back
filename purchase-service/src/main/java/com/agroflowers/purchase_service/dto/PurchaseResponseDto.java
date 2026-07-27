package com.agroflowers.purchase_service.dto;

import java.time.LocalDate;
import java.util.List;

import com.agroflowers.purchase_service.model.PurchaseStatus;

public record PurchaseResponseDto(
        Long id,
        String purchaseNumber,
        LocalDate purchaseDate,
        Long farmId,
        String farmName,
        PurchaseStatus status,
        String responsible,
        String observation,
        Double total,
        List<PurchaseDetailResponseDto> details
) {
}
