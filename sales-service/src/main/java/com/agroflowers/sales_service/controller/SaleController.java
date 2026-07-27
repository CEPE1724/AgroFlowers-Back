package com.agroflowers.sales_service.controller;

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

import com.agroflowers.sales_service.dto.PagedResponseDto;
import com.agroflowers.sales_service.dto.SaleRequestDto;
import com.agroflowers.sales_service.dto.SaleResponseDto;
import com.agroflowers.sales_service.service.SaleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<SaleResponseDto>> listSales(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(saleService.listSales(page, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDto> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping
    public ResponseEntity<SaleResponseDto> createSale(
            @Valid @RequestBody SaleRequestDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        SaleResponseDto created = saleService.createSale(request, bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
