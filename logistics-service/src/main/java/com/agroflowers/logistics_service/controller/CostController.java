package com.agroflowers.logistics_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.logistics_service.dto.CostRequestDto;
import com.agroflowers.logistics_service.dto.CostResponseDto;
import com.agroflowers.logistics_service.dto.PagedResponseDto;
import com.agroflowers.logistics_service.service.CostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/costs")
public class CostController {

    private final CostService costService;

    public CostController(CostService costService) {
        this.costService = costService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<CostResponseDto>> listCosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(costService.listCosts(page, pageSize, search));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CostResponseDto>> listAllCosts() {
        return ResponseEntity.ok(costService.listAllCosts());
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<CostResponseDto> getCostByShipmentId(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(costService.getCostByShipmentId(shipmentId));
    }

    @PostMapping
    public ResponseEntity<CostResponseDto> createCost(@Valid @RequestBody CostRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(costService.createCost(request));
    }
}
