package com.agroflowers.purchase_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.purchase_service.model.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Page<Purchase> findByPurchaseNumberContainingIgnoreCaseOrFarmNameContainingIgnoreCaseOrResponsibleContainingIgnoreCase(
            String purchaseNumber, String farmName, String responsible, Pageable pageable);
}
