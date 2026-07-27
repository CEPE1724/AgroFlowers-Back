package com.agroflowers.catalog_service.model;

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
@Table(name = "flowers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "flower_type", nullable = false, length = 100)
    private String flowerType;

    @Column(nullable = false, length = 100)
    private String variety;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(name = "stem_length", nullable = false)
    private Integer stemLength;

    @Column(name = "stems_per_bouquet", nullable = false)
    private Integer stemsPerBouquet;

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_unit", nullable = false, length = 20)
    private PurchaseUnit purchaseUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;
}
