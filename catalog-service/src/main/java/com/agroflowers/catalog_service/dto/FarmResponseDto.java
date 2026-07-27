package com.agroflowers.catalog_service.dto;

import com.agroflowers.catalog_service.model.FarmRating;
import com.agroflowers.catalog_service.model.RecordStatus;

public record FarmResponseDto(
        Long id,
        String code,
        String name,
        String ruc,
        String contactName,
        String phone,
        String email,
        String city,
        String province,
        String address,
        Integer creditDays,
        FarmRating rating,
        RecordStatus status,
        String observation,
        Double profitMargin
) {
}
