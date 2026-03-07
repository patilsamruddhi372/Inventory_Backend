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

    // =========================
    // PRICES
    // =========================

    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private BigDecimal mrpPrice;

    // =========================
    // INVENTORY
    // =========================

    private BigDecimal openingStock;   // initial stock
    private BigDecimal currentStock;   // fetched from stock table

    private Integer lowStockAlert;

    // =========================
    // TAX
    // =========================

    private BigDecimal gstRate;
    private String hsn;

    // =========================
    // FLAGS
    // =========================

    private Boolean isActive;
    private Boolean isFavorite;

    // =========================
    // OPTIONAL COMPUTED (service layer)
    // =========================

    private String stockStatus;   // inStock | lowStock | outOfStock

    // =========================
    // TIMESTAMPS
    // =========================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}