package com.agroflowers.catalog_service.dto;

import com.agroflowers.catalog_service.model.FarmRating;
import com.agroflowers.catalog_service.model.RecordStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record FarmRequestDto(

        @NotBlank
        String name,

        @NotBlank
        @Pattern(regexp = "^\\d{13}$")
        String ruc,

        @NotBlank
        String contactName,

        @NotBlank
        @Pattern(regexp = "^\\d{10}$")
        String phone,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String city,

        @NotBlank
        String province,

        String address,

        @NotNull
        @Min(0)
        Integer creditDays,

        @NotNull
        FarmRating rating,

        @NotNull
        RecordStatus status,

        String observation
) {
}
