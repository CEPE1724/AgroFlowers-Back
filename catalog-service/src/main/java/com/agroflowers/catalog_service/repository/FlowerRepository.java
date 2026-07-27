package com.agroflowers.catalog_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.catalog_service.model.Flower;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Long> {

    Page<Flower> findByCodeContainingIgnoreCaseOrFlowerTypeContainingIgnoreCaseOrVarietyContainingIgnoreCaseOrColorContainingIgnoreCase(
            String code, String flowerType, String variety, String color, Pageable pageable);

    boolean existsByCode(String code);
}
