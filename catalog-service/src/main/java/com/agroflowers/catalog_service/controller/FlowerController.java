package com.agroflowers.catalog_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agroflowers.catalog_service.dto.FlowerRequestDto;
import com.agroflowers.catalog_service.dto.FlowerResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;
import com.agroflowers.catalog_service.service.FlowerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/flowers")
public class FlowerController {

    private final FlowerService flowerService;

    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<FlowerResponseDto>> listFlowers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(flowerService.listFlowers(page, pageSize, search));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlowerResponseDto>> listAllFlowers() {
        return ResponseEntity.ok(flowerService.listAllFlowers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlowerResponseDto> getFlowerById(@PathVariable Long id) {
        return ResponseEntity.ok(flowerService.getFlowerById(id));
    }

    @PostMapping
    public ResponseEntity<FlowerResponseDto> createFlower(@Valid @RequestBody FlowerRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flowerService.createFlower(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlowerResponseDto> updateFlower(
            @PathVariable Long id,
            @Valid @RequestBody FlowerRequestDto request
    ) {
        return ResponseEntity.ok(flowerService.updateFlower(id, request));
    }
}
