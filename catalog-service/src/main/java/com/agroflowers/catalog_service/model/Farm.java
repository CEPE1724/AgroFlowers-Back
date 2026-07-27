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
@Table(name = "farms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 13)
    private String ruc;

    @Column(name = "contact_name", nullable = false, length = 150)
    private String contactName;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String province;

    @Column(length = 250)
    private String address;

    @Column(name = "credit_days", nullable = false)
    private Integer creditDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FarmRating rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(length = 500)
    private String observation;

    @Column(name = "profit_margin", nullable = false)
    private Double profitMargin = 0.0;
}
