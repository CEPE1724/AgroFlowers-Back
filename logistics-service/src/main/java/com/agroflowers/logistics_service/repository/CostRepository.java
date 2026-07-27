package com.agroflowers.logistics_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.logistics_service.model.Cost;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {

    Optional<Cost> findByShipmentId(Long shipmentId);

    Page<Cost> findByShipmentNumberContainingIgnoreCase(String shipmentNumber, Pageable pageable);
}
