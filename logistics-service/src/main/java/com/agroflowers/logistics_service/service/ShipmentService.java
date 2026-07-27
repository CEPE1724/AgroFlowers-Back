package com.agroflowers.logistics_service.service;

import java.util.List;

import com.agroflowers.logistics_service.dto.PagedResponseDto;
import com.agroflowers.logistics_service.dto.ShipmentRequestDto;
import com.agroflowers.logistics_service.dto.ShipmentResponseDto;

public interface ShipmentService {

    PagedResponseDto<ShipmentResponseDto> listShipments(int page, int pageSize, String search);

    ShipmentResponseDto getShipmentById(Long id);

    ShipmentResponseDto createShipment(ShipmentRequestDto request, String bearerToken);

    ShipmentResponseDto updateShipment(Long id, ShipmentRequestDto request, String bearerToken);

    List<ShipmentResponseDto> listAllShipments();
}
