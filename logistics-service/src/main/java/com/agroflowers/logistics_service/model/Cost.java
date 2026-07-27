package com.agroflowers.logistics_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "costs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_id", nullable = false, unique = true)
    private Long shipmentId;

    @Column(name = "shipment_number", nullable = false, length = 20)
    private String shipmentNumber;

    @Column(name = "flower_cost", nullable = false)
    private Double flowerCost;

    @Column(name = "air_freight", nullable = false)
    private Double airFreight;

    @Column(nullable = false)
    private Integer boxes;

    @Column(name = "cost_per_box", nullable = false)
    private Double costPerBox;

    @Column(nullable = false)
    private Double packing;

    @Column(name = "cost_per_label", nullable = false)
    private Double costPerLabel;

    @Column(nullable = false)
    private Double labels;

    @Column(name = "tax_base", nullable = false)
    private Double taxBase;

    @Column(name = "tax_percentage", nullable = false)
    private Double taxPercentage;

    @Column(nullable = false)
    private Double taxes;

    @Column(name = "ground_transport", nullable = false)
    private Double groundTransport;

    @Column(nullable = false)
    private Double insurance;

    @Column(nullable = false)
    private Double handling;

    @Column(name = "other_costs", nullable = false)
    private Double otherCosts;

    @Column(name = "other_costs_description", length = 250)
    private String otherCostsDescription;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;
}
