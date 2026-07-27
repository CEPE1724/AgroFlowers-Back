package com.agroflowers.sales_service.dto;

import java.time.LocalDate;
import java.util.List;

import com.agroflowers.sales_service.model.PaymentStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SaleRequestDto(

        @NotNull
        @Min(1)
        Long shipmentId,

        @NotBlank
        String customer,

        @NotNull
        LocalDate saleDate,

        @NotBlank
        String currency,

        @NotNull
        PaymentStatus paymentStatus,

        String observation,

        @NotEmpty
        @Valid
        List<SaleDetailRequestDto> details
) {
}
