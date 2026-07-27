package com.agroflowers.purchase_service.service;

import com.agroflowers.purchase_service.dto.PagedResponseDto;
import com.agroflowers.purchase_service.dto.PurchaseRequestDto;
import com.agroflowers.purchase_service.dto.PurchaseResponseDto;

public interface PurchaseService {

    PagedResponseDto<PurchaseResponseDto> listPurchases(int page, int pageSize, String search);

    PurchaseResponseDto getPurchaseById(Long id);

    PurchaseResponseDto createPurchase(PurchaseRequestDto request, String bearerToken);
}
