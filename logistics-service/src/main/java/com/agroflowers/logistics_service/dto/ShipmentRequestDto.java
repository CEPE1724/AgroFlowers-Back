package com.agroflowers.logistics_service.dto;

import java.time.LocalDate;

import com.agroflowers.logistics_service.model.ShipmentStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShipmentRequestDto(

        @NotNull
        Long farmId,

        @NotNull
        LocalDate shipmentDate,

        @NotBlank
        String freightCompany,

        @NotBlank
        String airline,

        @NotBlank
        String airWaybill,

        @NotBlank
        String flightNumber,

        @NotBlank
        String origin,

        @NotBlank
        String destination,

        @NotBlank
        String customer,

        @NotNull
        @Min(1)
        Integer boxes,

        @NotNull
        @Positive
        Double actualWeight,

        @NotNull
        @Positive
        Double volumetricWeight,

        @NotNull
        @Positive
        Double ratePerKg,

        @NotNull
        ShipmentStatus status
) {
}
