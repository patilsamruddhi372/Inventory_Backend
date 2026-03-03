package com.Inventory.Inventory_Backend.item.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class itemResponceDTO {

    // ─── Identity ─────────────────────────────────
    private Long id;
    private String name;
    private String type;

    // ─── Details ──────────────────────────────────
    private String sku;
    private String category;
    private String brand;
    private String unit;

    // ─── Prices ───────────────────────────────────
    private BigDecimal salePrice;
    private BigDecimal purchasePrice;
    private BigDecimal mrpPrice;

    // ─── Stock ────────────────────────────────────
    private Integer stock;
    private Integer lowStockAlert;

    // ─── Tax ──────────────────────────────────────
    private BigDecimal gstRate;
    private String hsn;

    // ─── Flags ────────────────────────────────────
    private Boolean isActive;
    private Boolean isFavorite;

    // ─── Computed ─────────────────────────────────
    private BigDecimal stockValue;      // stock × salePrice
    private String stockStatus;         // "inStock" | "lowStock" | "outOfStock"

    // ─── Timestamps ───────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}