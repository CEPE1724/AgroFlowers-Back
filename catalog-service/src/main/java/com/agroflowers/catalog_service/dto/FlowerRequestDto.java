package com.agroflowers.catalog_service.dto;

import com.agroflowers.catalog_service.model.PurchaseUnit;
import com.agroflowers.catalog_service.model.RecordStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FlowerRequestDto(

        @NotBlank
        String flowerType,

        @NotBlank
        String variety,

        @NotBlank
        String color,

        @NotNull
        @Min(1)
        Integer stemLength,

        @NotNull
        @Min(1)
        Integer stemsPerBouquet,

        @NotNull
        PurchaseUnit purchaseUnit,

        @NotNull
        RecordStatus status
) {
}
