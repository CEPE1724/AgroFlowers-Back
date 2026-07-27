package com.agroflowers.purchase_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchase_details")
@Data
@NoArgsConstructor
public class PurchaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column(name = "flower_id", nullable = false)
    private Long flowerId;

    @Column(name = "flower_name", nullable = false, length = 150)
    private String flowerName;

    @Column(name = "quantity_bouquets", nullable = false)
    private Integer quantityBouquets;

    @Column(name = "stems_per_bouquet", nullable = false)
    private Integer stemsPerBouquet;

    @Column(name = "total_stems", nullable = false)
    private Integer totalStems;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Double subtotal;
}
