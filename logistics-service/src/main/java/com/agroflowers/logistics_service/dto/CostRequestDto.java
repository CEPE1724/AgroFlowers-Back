package com.agroflowers.logistics_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CostRequestDto(

        @NotNull
        @Min(1)
        Long shipmentId,

        @NotNull
        @Min(0)
        Double flowerCost,

        @NotNull
        @Min(0)
        Double airFreight,

        @NotNull
        @Min(1)
        Integer boxes,

        @NotNull
        @Min(0)
        Double costPerBox,

        @NotNull
        @Min(0)
        Double costPerLabel,

        @NotNull
        @Min(0)
        Double taxBase,

        @NotNull
        @Min(0)
        Double taxPercentage,

        @NotNull
        @Min(0)
        Double groundTransport,

        @NotNull
        @Min(0)
        Double insurance,

        @NotNull
        @Min(0)
        Double handling,

        @NotNull
        @Min(0)
        Double otherCosts,

        String otherCostsDescription
) {
}
