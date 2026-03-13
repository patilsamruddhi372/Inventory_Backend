package com.Inventory.Inventory_Backend.item.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDTO {

    // =========================
    // IDENTITY
    // =========================

    private Long id;
    private String name;
    private String type;

    // =========================
    // DETAILS
    // =========================

    private String sku;
    private String category;
    private String brand;
    private String unit;
    private String description;

    // =========================
    // PRICES
    // =========================

    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private BigDecimal mrpPrice;

    // =========================
    // INVENTORY
    // =========================

    private BigDecimal openingStock;
    private BigDecimal currentStock;
    private Integer lowStockAlert;

    // =========================
    // TAX
    // =========================

    private BigDecimal gstRate;
    private String hsn;

    // =========================
    // DISCOUNTS (added)
    // =========================

    private BigDecimal saleDiscountPercent;
    private BigDecimal saleDiscountAmount;
    private BigDecimal purchaseDiscountPercent;
    private BigDecimal purchaseDiscountAmount;

    // =========================
    // FLAGS
    // =========================

    private Boolean isActive;
    private Boolean isFavorite;

    // =========================
    // COMPUTED
    // =========================

    private String stockStatus;

    // =========================
    // TIMESTAMPS
    // =========================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}