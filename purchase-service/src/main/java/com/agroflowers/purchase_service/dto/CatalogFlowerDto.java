package com.agroflowers.purchase_service.dto;

public record CatalogFlowerDto(
        Long id,
        String flowerType,
        String variety,
        Integer stemLength
) {
}
