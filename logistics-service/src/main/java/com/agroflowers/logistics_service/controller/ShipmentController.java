package com.agroflowers.logistics_service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.logistics_service.dto.PagedResponseDto;
import com.agroflowers.logistics_service.dto.ShipmentRequestDto;
import com.agroflowers.logistics_service.dto.ShipmentResponseDto;
import com.agroflowers.logistics_service.service.ShipmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<ShipmentResponseDto>> listShipments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(shipmentService.listShipments(page, pageSize, search));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShipmentResponseDto>> listAllShipments() {
        return ResponseEntity.ok(shipmentService.listAllShipments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getShipmentById(id));
    }

    @PostMapping
    public ResponseEntity<ShipmentResponseDto> createShipment(
            @Valid @RequestBody ShipmentRequestDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.createShipment(request, bearerToken));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponseDto> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentRequestDto request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, request, bearerToken));
    }
}
