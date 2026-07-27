package com.agroflowers.purchase_service.service.impl;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.purchase_service.client.CatalogClient;
import com.agroflowers.purchase_service.dto.PagedResponseDto;
import com.agroflowers.purchase_service.dto.PurchaseDetailRequestDto;
import com.agroflowers.purchase_service.dto.PurchaseDetailResponseDto;
import com.agroflowers.purchase_service.dto.PurchaseRequestDto;
import com.agroflowers.purchase_service.dto.PurchaseResponseDto;
import com.agroflowers.purchase_service.model.Purchase;
import com.agroflowers.purchase_service.model.PurchaseDetail;
import com.agroflowers.purchase_service.model.PurchaseStatus;
import com.agroflowers.purchase_service.repository.PurchaseRepository;
import com.agroflowers.purchase_service.service.PurchaseService;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CatalogClient catalogClient;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, CatalogClient catalogClient) {
        this.purchaseRepository = purchaseRepository;
        this.catalogClient = catalogClient;
    }

    @Override
    public PagedResponseDto<PurchaseResponseDto> listPurchases(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Purchase> result = StringUtils.hasText(search)
                ? purchaseRepository.findByPurchaseNumberContainingIgnoreCaseOrFarmNameContainingIgnoreCaseOrResponsibleContainingIgnoreCase(
                        search, search, search, pageable)
                : purchaseRepository.findAll(pageable);

        List<PurchaseResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    public PurchaseResponseDto getPurchaseById(Long id) {
        return toResponseDto(findPurchaseOrThrow(id));
    }

    @Override
    public PurchaseResponseDto createPurchase(PurchaseRequestDto request, String bearerToken) {
        String farmName = catalogClient.getFarmName(request.farmId(), bearerToken);
        Map<Long, String> flowerNames = catalogClient.getFlowerDisplayNames(bearerToken);

        Purchase purchase = new Purchase();
        purchase.setPurchaseNumber(generatePurchaseNumber());
        purchase.setPurchaseDate(request.purchaseDate());
        purchase.setFarmId(request.farmId());
        purchase.setFarmName(farmName);
        purchase.setStatus(PurchaseStatus.REGISTERED);
        purchase.setResponsible(request.responsible());
        purchase.setObservation(request.observation());

        double total = 0.0;

        for (PurchaseDetailRequestDto detailRequest : request.details()) {
            PurchaseDetail detail = new PurchaseDetail();
            detail.setPurchase(purchase);
            detail.setFlowerId(detailRequest.flowerId());
            detail.setFlowerName(flowerNames.getOrDefault(detailRequest.flowerId(), "Variedad"));
            detail.setQuantityBouquets(detailRequest.quantityBouquets());
            detail.setStemsPerBouquet(detailRequest.stemsPerBouquet());
            detail.setTotalStems(detailRequest.quantityBouquets() * detailRequest.stemsPerBouquet());

            double subtotal = round2(detailRequest.quantityBouquets() * detailRequest.unitPrice());
            detail.setUnitPrice(detailRequest.unitPrice());
            detail.setSubtotal(subtotal);

            total += subtotal;
            purchase.getDetails().add(detail);
        }

        purchase.setTotal(round2(total));

        return toResponseDto(purchaseRepository.save(purchase));
    }

    private String generatePurchaseNumber() {
        long nextSequence = purchaseRepository.count() + 1;
        return "CMP-" + String.format("%06d", nextSequence);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Purchase findPurchaseOrThrow(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro la compra con id: " + id));
    }

    private PurchaseResponseDto toResponseDto(Purchase purchase) {
        List<PurchaseDetailResponseDto> details = purchase.getDetails().stream()
                .map(detail -> new PurchaseDetailResponseDto(
                        detail.getFlowerId(),
                        detail.getFlowerName(),
                        detail.getQuantityBouquets(),
                        detail.getStemsPerBouquet(),
                        detail.getTotalStems(),
                        detail.getUnitPrice(),
                        detail.getSubtotal()
                ))
                .toList();

        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getPurchaseNumber(),
                purchase.getPurchaseDate(),
                purchase.getFarmId(),
                purchase.getFarmName(),
                purchase.getStatus(),
                purchase.getResponsible(),
                purchase.getObservation(),
                purchase.getTotal(),
                details
        );
    }
}
