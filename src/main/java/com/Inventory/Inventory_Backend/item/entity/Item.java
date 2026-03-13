package com.Inventory.Inventory_Backend.item.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // TENANT FIELD
    // =========================

    @Column(name = "business_id")
    private Long businessId;

    // =========================
    // BASIC INFO
    // =========================

    @Column(nullable = false)
    private String name;

    // GOODS / SERVICE
    private String type;

    private String category;

    private String sku;

    private String unit;

    @Column(length = 500)
    private String description;


    // =========================
    // INVENTORY INFO
    // =========================

    // Initial quantity when item created
    private BigDecimal openingStock;

    private Integer lowStockAlert;

    // =========================
    // PRICING
    // =========================

    private BigDecimal salePrice;

    private BigDecimal purchasePrice;

    private BigDecimal gstRate;

    private BigDecimal mrpPrice;

    // =========================
    // DEFAULT DISCOUNTS
    // =========================

    private BigDecimal saleDiscountPercent;

    private BigDecimal saleDiscountAmount;

    private BigDecimal purchaseDiscountPercent;

    private BigDecimal purchaseDiscountAmount;

    // =========================
    // DETAILS
    // =========================

    private String brand;

    @Column(name = "hsn_code")
    private String hsn;

    // =========================
    // FLAGS
    // =========================

    private Boolean isFavorite = false;

    private Boolean isActive = true;

    // =========================
    // AUDIT FIELDS
    // =========================

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}