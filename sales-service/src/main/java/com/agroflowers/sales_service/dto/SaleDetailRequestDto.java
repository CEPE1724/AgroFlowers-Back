package com.agroflowers.sales_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaleDetailRequestDto(

        @NotNull
        @Min(1)
        Long productId,

        @NotBlank
        String productName,

        @NotNull
        @Min(1)
        Integer quantity,

        @NotNull
        @Min(0)
        Double unitPrice
) {
}
