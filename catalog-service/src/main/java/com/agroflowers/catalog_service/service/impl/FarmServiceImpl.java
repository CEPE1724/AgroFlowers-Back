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

import com.agroflowers.catalog_service.dto.FarmRequestDto;
import com.agroflowers.catalog_service.dto.FarmResponseDto;
import com.agroflowers.catalog_service.dto.PagedResponseDto;
import com.agroflowers.catalog_service.model.Farm;
import com.agroflowers.catalog_service.model.RecordStatus;
import com.agroflowers.catalog_service.repository.FarmRepository;
import com.agroflowers.catalog_service.service.FarmService;

@Service
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;

    public FarmServiceImpl(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @Override
    public PagedResponseDto<FarmResponseDto> listFarms(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Farm> result = StringUtils.hasText(search)
                ? farmRepository
                        .findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrContactNameContainingIgnoreCase(
                                search, search, search, search, pageable)
                : farmRepository.findAll(pageable);

        List<FarmResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    @Cacheable(cacheNames = "farmById", key = "#id")
    public FarmResponseDto getFarmById(Long id) {
        Farm farm = findFarmOrThrow(id);
        return toResponseDto(farm);
    }

    @Override
    @CacheEvict(cacheNames = { "farms", "farmById" }, allEntries = true)
    public FarmResponseDto createFarm(FarmRequestDto request) {
        Farm farm = new Farm();
        applyRequest(farm, request);
        farm.setCode(generateFarmCode());
        farm.setProfitMargin(0.0);

        return toResponseDto(farmRepository.save(farm));
    }

    @Override
    @CacheEvict(cacheNames = { "farms", "farmById" }, allEntries = true)
    public FarmResponseDto updateFarm(Long id, FarmRequestDto request) {
        Farm farm = findFarmOrThrow(id);
        applyRequest(farm, request);

        return toResponseDto(farmRepository.save(farm));
    }

    @Override
    @CacheEvict(cacheNames = { "farms", "farmById" }, allEntries = true)
    public void deactivateFarm(Long id) {
        Farm farm = findFarmOrThrow(id);
        farm.setStatus(RecordStatus.INACTIVE);
        farmRepository.save(farm);
    }

    @Override
    @Cacheable(cacheNames = "farms")
    public List<FarmResponseDto> listAllFarms() {
        return farmRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    private Farm findFarmOrThrow(Long id) {
        return farmRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro la finca con id: " + id));
    }

    private String generateFarmCode() {
        long next = farmRepository.count() + 1;
        String code = String.format("FIN-%03d", next);

        while (farmRepository.existsByCode(code)) {
            next++;
            code = String.format("FIN-%03d", next);
        }

        return code;
    }

    private void applyRequest(Farm farm, FarmRequestDto request) {
        farm.setName(request.name());
        farm.setRuc(request.ruc());
        farm.setContactName(request.contactName());
        farm.setPhone(request.phone());
        farm.setEmail(request.email());
        farm.setCity(request.city());
        farm.setProvince(request.province());
        farm.setAddress(request.address());
        farm.setCreditDays(request.creditDays());
        farm.setRating(request.rating());
        farm.setStatus(request.status());
        farm.setObservation(request.observation());
    }

    private FarmResponseDto toResponseDto(Farm farm) {
        return new FarmResponseDto(
                farm.getId(),
                farm.getCode(),
                farm.getName(),
                farm.getRuc(),
                farm.getContactName(),
                farm.getPhone(),
                farm.getEmail(),
                farm.getCity(),
                farm.getProvince(),
                farm.getAddress(),
                farm.getCreditDays(),
                farm.getRating(),
                farm.getStatus(),
                farm.getObservation(),
                farm.getProfitMargin()
        );
    }
}
