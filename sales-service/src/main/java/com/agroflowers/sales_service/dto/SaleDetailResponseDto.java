package com.agroflowers.sales_service.dto;

public record SaleDetailResponseDto(
        Long productId,
        String productName,
        Integer quantity,
        Double unitPrice,
        Double subtotal
) {
}
