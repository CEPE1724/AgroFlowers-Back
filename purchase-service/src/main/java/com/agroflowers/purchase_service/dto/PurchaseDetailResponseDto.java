package com.agroflowers.purchase_service.dto;

public record PurchaseDetailResponseDto(
        Long flowerId,
        String flowerName,
        Integer quantityBouquets,
        Integer stemsPerBouquet,
        Integer totalStems,
        Double unitPrice,
        Double subtotal
) {
}
