package com.agroflowers.catalog_service.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.catalog_service.dto.FlowerRequestDto;
import com.agroflowers.catalog_service.dto.FlowerResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;
import com.agroflowers.catalog_service.model.Flower;
import com.agroflowers.catalog_service.repository.FlowerRepository;
import com.agroflowers.catalog_service.service.FlowerService;

@Service
public class FlowerServiceImpl implements FlowerService {

    private final FlowerRepository flowerRepository;

    public FlowerServiceImpl(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @Override
    public PagedResponseDto<FlowerResponseDto> listFlowers(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Flower> result = StringUtils.hasText(search)
                ? flowerRepository
                        .findByCodeContainingIgnoreCaseOrFlowerTypeContainingIgnoreCaseOrVarietyContainingIgnoreCaseOrColorContainingIgnoreCase(
                                search, search, search, search, pageable)
                : flowerRepository.findAll(pageable);

        List<FlowerResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    @Cacheable(cacheNames = "flowerById", key = "#id")
    public FlowerResponseDto getFlowerById(Long id) {
        return toResponseDto(findFlowerOrThrow(id));
    }

    @Override
    @CacheEvict(cacheNames = { "flowers", "flowerById" }, allEntries = true)
    public FlowerResponseDto createFlower(FlowerRequestDto request) {
        Flower flower = new Flower();
        applyRequest(flower, request);
        flower.setCode(generateFlowerCode(request));

        return toResponseDto(flowerRepository.save(flower));
    }

    @Override
    @CacheEvict(cacheNames = { "flowers", "flowerById" }, allEntries = true)
    public FlowerResponseDto updateFlower(Long id, FlowerRequestDto request) {
        Flower flower = findFlowerOrThrow(id);
        applyRequest(flower, request);

        return toResponseDto(flowerRepository.save(flower));
    }

    @Override
    @Cacheable(cacheNames = "flowers")
    public List<FlowerResponseDto> listAllFlowers() {
        return flowerRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    private Flower findFlowerOrThrow(Long id) {
        return flowerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro la variedad con id: " + id));
    }

    private String generateFlowerCode(FlowerRequestDto request) {
        String base = codePart(request.flowerType()) + "-" + codePart(request.variety()) + "-" + request.stemLength();
        String code = base;
        int suffix = 1;

        while (flowerRepository.existsByCode(code)) {
            suffix++;
            code = base + "-" + suffix;
        }

        return code;
    }

    private String codePart(String value) {
        String cleaned = value.trim().toUpperCase().replaceAll("[^A-Z]", "");
        return cleaned.length() >= 3 ? cleaned.substring(0, 3) : cleaned;
    }

    private void applyRequest(Flower flower, FlowerRequestDto request) {
        flower.setFlowerType(request.flowerType());
        flower.setVariety(request.variety());
        flower.setColor(request.color());
        flower.setStemLength(request.stemLength());
        flower.setStemsPerBouquet(request.stemsPerBouquet());
        flower.setPurchaseUnit(request.purchaseUnit());
        flower.setStatus(request.status());
    }

    private FlowerResponseDto toResponseDto(Flower flower) {
        return new FlowerResponseDto(
                flower.getId(),
                flower.getCode(),
                flower.getFlowerType(),
                flower.getVariety(),
                flower.getColor(),
                flower.getStemLength(),
                flower.getStemsPerBouquet(),
                flower.getPurchaseUnit(),
                flower.getStatus()
        );
    }
}
