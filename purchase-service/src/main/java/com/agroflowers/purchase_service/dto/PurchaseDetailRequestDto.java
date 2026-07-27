package com.agroflowers.purchase_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseDetailRequestDto(

        @NotNull
        @Min(1)
        Long flowerId,

        @NotNull
        @Min(1)
        Integer quantityBouquets,

        @NotNull
        @Min(1)
        Integer stemsPerBouquet,

        @NotNull
        @Min(0)
        Double unitPrice
) {
}
