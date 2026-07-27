package com.agroflowers.logistics_service.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.agroflowers.logistics_service.client.CatalogClient;
import com.agroflowers.logistics_service.dto.PagedResponseDto;
import com.agroflowers.logistics_service.dto.ShipmentRequestDto;
import com.agroflowers.logistics_service.dto.ShipmentResponseDto;
import com.agroflowers.logistics_service.model.Shipment;
import com.agroflowers.logistics_service.repository.ShipmentRepository;
import com.agroflowers.logistics_service.service.ShipmentService;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CatalogClient catalogClient;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository, CatalogClient catalogClient) {
        this.shipmentRepository = shipmentRepository;
        this.catalogClient = catalogClient;
    }

    @Override
    public PagedResponseDto<ShipmentResponseDto> listShipments(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);

        Page<Shipment> result = StringUtils.hasText(search)
                ? shipmentRepository.findByShipmentNumberContainingIgnoreCaseOrCustomerContainingIgnoreCaseOrDestinationContainingIgnoreCaseOrAirlineContainingIgnoreCase(
                        search, search, search, search, pageable)
                : shipmentRepository.findAll(pageable);

        List<ShipmentResponseDto> items = result.getContent().stream().map(this::toResponseDto).toList();

        return new PagedResponseDto<>(items, result.getTotalElements(), page, pageSize);
    }

    @Override
    public ShipmentResponseDto getShipmentById(Long id) {
        return toResponseDto(findShipmentOrThrow(id));
    }

    @Override
    public ShipmentResponseDto createShipment(ShipmentRequestDto request, String bearerToken) {
        Shipment shipment = new Shipment();
        shipment.setShipmentNumber(generateShipmentNumber());
        applyRequest(shipment, request, bearerToken);

        return toResponseDto(shipmentRepository.save(shipment));
    }

    @Override
    public ShipmentResponseDto updateShipment(Long id, ShipmentRequestDto request, String bearerToken) {
        Shipment shipment = findShipmentOrThrow(id);
        applyRequest(shipment, request, bearerToken);

        return toResponseDto(shipmentRepository.save(shipment));
    }

    @Override
    public List<ShipmentResponseDto> listAllShipments() {
        return shipmentRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    private String generateShipmentNumber() {
        long nextSequence = shipmentRepository.count() + 1;
        return "EMB-" + String.format("%06d", nextSequence);
    }

    private void applyRequest(Shipment shipment, ShipmentRequestDto request, String bearerToken) {
        double billableWeight = Math.max(request.actualWeight(), request.volumetricWeight());
        double estimatedFreight = round2(billableWeight * request.ratePerKg());

        shipment.setFarmId(request.farmId());
        shipment.setFarmName(catalogClient.getFarmName(request.farmId(), bearerToken));
        shipment.setShipmentDate(request.shipmentDate());
        shipment.setFreightCompany(request.freightCompany());
        shipment.setAirline(request.airline());
        shipment.setAirWaybill(request.airWaybill());
        shipment.setFlightNumber(request.flightNumber());
        shipment.setOrigin(request.origin());
        shipment.setDestination(request.destination());
        shipment.setCustomer(request.customer());
        shipment.setBoxes(request.boxes());
        shipment.setActualWeight(request.actualWeight());
        shipment.setVolumetricWeight(request.volumetricWeight());
        shipment.setBillableWeight(billableWeight);
        shipment.setRatePerKg(request.ratePerKg());
        shipment.setEstimatedFreight(estimatedFreight);
        shipment.setStatus(request.status());
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Shipment findShipmentOrThrow(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro el embarque con id: " + id));
    }

    private ShipmentResponseDto toResponseDto(Shipment shipment) {
        return new ShipmentResponseDto(
                shipment.getId(),
                shipment.getShipmentNumber(),
                shipment.getFarmId(),
                shipment.getFarmName(),
                shipment.getShipmentDate(),
                shipment.getFreightCompany(),
                shipment.getAirline(),
                shipment.getAirWaybill(),
                shipment.getFlightNumber(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getCustomer(),
                shipment.getBoxes(),
                shipment.getActualWeight(),
                shipment.getVolumetricWeight(),
                shipment.getBillableWeight(),
                shipment.getRatePerKg(),
                shipment.getEstimatedFreight(),
                shipment.getStatus()
        );
    }
}
