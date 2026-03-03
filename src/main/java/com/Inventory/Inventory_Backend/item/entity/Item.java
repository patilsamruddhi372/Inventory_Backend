package com.Inventory.Inventory_Backend.item.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // maps to business_id
    @Column(name = "business_id")
    private Long businessId;

    // basic
    private String name;          // NOT NULL in DB
    private String type;          // goods / service
    private String category;
    private String sku;
    private String unit;

    // stock
    private Integer stock;
    private Integer lowStockAlert;

    // prices
    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private BigDecimal gstRate;
    private BigDecimal mrpPrice;

    // discounts (default sale/purchase discounts for this item)
    private BigDecimal saleDiscountPercent;
    private BigDecimal saleDiscountAmount;
    private BigDecimal purchaseDiscountPercent;
    private BigDecimal purchaseDiscountAmount;

    // details
    private String brand;

    // maps to hsn_code column in DB
    @Column(name = "hsn_code")
    private String hsn;

    // flags
    private Boolean isFavorite = false;
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}