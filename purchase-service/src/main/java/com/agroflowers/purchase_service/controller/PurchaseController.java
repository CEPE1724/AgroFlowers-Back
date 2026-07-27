package com.agroflowers.purchase_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.purchase_service.dto.PagedResponseDto;
import com.agroflowers.purchase_service.dto.PurchaseRequestDto;
import com.agroflowers.purchase_service.dto.PurchaseResponseDto;
import com.agroflowers.purchase_service.service.PurchaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<PurchaseResponseDto>> listPurchases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(purchaseService.listPurchases(page, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponseDto> createPurchase(
            @Valid @RequestBody PurchaseRequestDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        PurchaseResponseDto created = purchaseService.createPurchase(request, bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
