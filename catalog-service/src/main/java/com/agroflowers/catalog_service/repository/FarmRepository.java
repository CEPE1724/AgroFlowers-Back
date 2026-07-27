package com.agroflowers.catalog_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflowers.catalog_service.model.Farm;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {

    Page<Farm> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrContactNameContainingIgnoreCase(
            String name, String code, String city, String contactName, Pageable pageable);

    boolean existsByCode(String code);
}
