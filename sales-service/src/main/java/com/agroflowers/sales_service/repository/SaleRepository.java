package com.agroflowers.sales_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.sales_service.model.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Page<Sale> findBySaleNumberContainingIgnoreCaseOrCustomerContainingIgnoreCaseOrShipmentNumberContainingIgnoreCase(
            String saleNumber, String customer, String shipmentNumber, Pageable pageable);
}
