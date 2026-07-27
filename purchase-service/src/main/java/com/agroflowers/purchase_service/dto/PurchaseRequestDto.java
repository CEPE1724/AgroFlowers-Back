package com.agroflowers.purchase_service.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequestDto(

        @NotNull
        LocalDate purchaseDate,

        @NotNull
        @Min(1)
        Long farmId,

        @NotNull
        String responsible,

        String observation,

        @NotEmpty
        @Valid
        List<PurchaseDetailRequestDto> details
) {
}
