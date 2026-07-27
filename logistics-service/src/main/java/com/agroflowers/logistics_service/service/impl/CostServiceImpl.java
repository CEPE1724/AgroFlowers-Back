package com.agroflowers.logistics_service.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.logistics_service.dto.CostRequestDto;
import com.agroflowers.logistics_service.dto.CostResponseDto;
import com.agroflowers.logistics_service.dto.PagedResponseDto;
import com.agroflowers.logistics_service.model.Cost;
import com.agroflowers.logistics_service.model.Shipment;
import com.agroflowers.logistics_service.repository.CostRepository;
import com.agroflowers.logistics_service.repository.ShipmentRepository;
import com.agroflowers.logistics_service.service.CostService;

@Service
public class CostServiceImpl implements CostService {

    private final CostRepository costRepository;
    private final ShipmentRepository shipmentRepository;

    public CostServiceImpl(CostRepository costRepository, ShipmentRepository shipmentRepository) {
        this.costRepository = costRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public PagedResponseDto<CostResponseDto> listCosts(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Cost> result = StringUtils.hasText(search)
                ? costRepository.findByShipmentNumberContainingIgnoreCase(search, pageable)
                : costRepository.findAll(pageable);

        List<CostResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    public CostResponseDto getCostByShipmentId(Long shipmentId) {
        Cost cost = costRepository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new NoSuchElementException("El embarque no tiene costos registrados"));

        return toResponseDto(cost);
    }

    @Override
    public CostResponseDto createCost(CostRequestDto request) {
        Shipment shipment = shipmentRepository.findById(request.shipmentId())
                .orElseThrow(() -> new NoSuchElementException("No se encontro el embarque con id: " + request.shipmentId()));

        if (costRepository.findByShipmentId(request.shipmentId()).isPresent()) {
            throw new IllegalStateException("El embarque ya tiene costos registrados");
        }

        double packing = round2(request.boxes() * request.costPerBox());
        double labels = round2(request.boxes() * request.costPerLabel());
        double taxes = round2(request.taxBase() * request.taxPercentage() / 100);

        double totalCost = round2(
                request.flowerCost()
                        + request.airFreight()
                        + packing
                        + labels
                        + taxes
                        + request.groundTransport()
                        + request.insurance()
                        + request.handling()
                        + request.otherCosts()
        );

        Cost cost = new Cost();
        cost.setShipmentId(shipment.getId());
        cost.setShipmentNumber(shipment.getShipmentNumber());
        cost.setFlowerCost(request.flowerCost());
        cost.setAirFreight(request.airFreight());
        cost.setBoxes(request.boxes());
        cost.setCostPerBox(request.costPerBox());
        cost.setPacking(packing);
        cost.setCostPerLabel(request.costPerLabel());
        cost.setLabels(labels);
        cost.setTaxBase(request.taxBase());
        cost.setTaxPercentage(request.taxPercentage());
        cost.setTaxes(taxes);
        cost.setGroundTransport(request.groundTransport());
        cost.setInsurance(request.insurance());
        cost.setHandling(request.handling());
        cost.setOtherCosts(request.otherCosts());
        cost.setOtherCostsDescription(request.otherCostsDescription());
        cost.setTotalCost(totalCost);

        return toResponseDto(costRepository.save(cost));
    }

    @Override
    public List<CostResponseDto> listAllCosts() {
        return costRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private CostResponseDto toResponseDto(Cost cost) {
        return new CostResponseDto(
                cost.getId(),
                cost.getShipmentId(),
                cost.getShipmentNumber(),
                cost.getFlowerCost(),
                cost.getAirFreight(),
                cost.getBoxes(),
                cost.getCostPerBox(),
                cost.getPacking(),
                cost.getCostPerLabel(),
                cost.getLabels(),
                cost.getTaxBase(),
                cost.getTaxPercentage(),
                cost.getTaxes(),
                cost.getGroundTransport(),
                cost.getInsurance(),
                cost.getHandling(),
                cost.getOtherCosts(),
                cost.getOtherCostsDescription(),
                cost.getTotalCost()
        );
    }
}
