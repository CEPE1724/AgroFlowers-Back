package com.agroflowers.logistics_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.logistics_service.model.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Page<Shipment> findByShipmentNumberContainingIgnoreCaseOrCustomerContainingIgnoreCaseOrDestinationContainingIgnoreCaseOrAirlineContainingIgnoreCase(
            String shipmentNumber, String customer, String destination, String airline, Pageable pageable);
}
