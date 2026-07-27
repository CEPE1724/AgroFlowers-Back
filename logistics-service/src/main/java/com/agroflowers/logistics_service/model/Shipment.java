package com.agroflowers.logistics_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_number", nullable = false, unique = true, length = 20)
    private String shipmentNumber;

    @Column(name = "farm_id", nullable = false)
    private Long farmId;

    @Column(name = "farm_name", nullable = false, length = 150)
    private String farmName;

    @Column(name = "shipment_date", nullable = false)
    private LocalDate shipmentDate;

    @Column(name = "freight_company", nullable = false, length = 150)
    private String freightCompany;

    @Column(nullable = false, length = 150)
    private String airline;

    @Column(name = "air_waybill", nullable = false, length = 50)
    private String airWaybill;

    @Column(name = "flight_number", nullable = false, length = 30)
    private String flightNumber;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false, length = 150)
    private String customer;

    @Column(nullable = false)
    private Integer boxes;

    @Column(name = "actual_weight", nullable = false)
    private Double actualWeight;

    @Column(name = "volumetric_weight", nullable = false)
    private Double volumetricWeight;

    @Column(name = "billable_weight", nullable = false)
    private Double billableWeight;

    @Column(name = "rate_per_kg", nullable = false)
    private Double ratePerKg;

    @Column(name = "estimated_freight", nullable = false)
    private Double estimatedFreight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShipmentStatus status = ShipmentStatus.DRAFT;
}
