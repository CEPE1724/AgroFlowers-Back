package com.agroflowers.catalog_service.dto;

import com.agroflowers.catalog_service.model.PurchaseUnit;
import com.agroflowers.catalog_service.model.RecordStatus;

public record FlowerResponseDto(
        Long id,
        String code,
        String flowerType,
        String variety,
        String color,
        Integer stemLength,
        Integer stemsPerBouquet,
        PurchaseUnit purchaseUnit,
        RecordStatus status
) {
}
