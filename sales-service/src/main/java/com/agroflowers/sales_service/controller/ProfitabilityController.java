package com.agroflowers.sales_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.sales_service.dto.ProfitabilityResponseDto;
import com.agroflowers.sales_service.model.ProfitabilityClassification;
import com.agroflowers.sales_service.service.ProfitabilityService;

@RestController
@RequestMapping("/api/profitability")
public class ProfitabilityController {

    private final ProfitabilityService profitabilityService;

    public ProfitabilityController(ProfitabilityService profitabilityService) {
        this.profitabilityService = profitabilityService;
    }

    @GetMapping
    public ResponseEntity<List<ProfitabilityResponseDto>> listProfitability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String farmName,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) String shipmentNumber,
            @RequestParam(required = false) ProfitabilityClassification classification,
            @RequestParam(required = false) Double minMargin,
            @RequestParam(required = false) Double maxMargin,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        List<ProfitabilityResponseDto> records = profitabilityService.listProfitability(
                dateFrom, dateTo, farmName, customer, shipmentNumber, classification, minMargin, maxMargin, bearerToken
        );

        return ResponseEntity.ok(records);
    }
}
