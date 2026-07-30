package com.agroflowers.catalog_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.catalog_service.dto.FarmRequestDto;
import com.agroflowers.catalog_service.dto.FarmResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;
import com.agroflowers.catalog_service.service.FarmService;

import jakarta.validation.Valid;
   
@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<FarmResponseDto>> listFarms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(farmService.listFarms(page, pageSize, search));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FarmResponseDto>> listAllFarms() {
        return ResponseEntity.ok(farmService.listAllFarms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponseDto> getFarmById(@PathVariable Long id) {
        return ResponseEntity.ok(farmService.getFarmById(id));
    }

    @PostMapping
    public ResponseEntity<FarmResponseDto> createFarm(@Valid @RequestBody FarmRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(farmService.createFarm(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmResponseDto> updateFarm(
            @PathVariable Long id,
            @Valid @RequestBody FarmRequestDto request
    ) {
        return ResponseEntity.ok(farmService.updateFarm(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateFarm(@PathVariable Long id) {
        farmService.deactivateFarm(id);
        return ResponseEntity.noContent().build();
    }
}
