package com.Inventory.Inventory_Backend.item.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDTO {

    // ===============================
    // REQUIRED
    // ===============================


    @NotBlank(message = "Item name is required")
    @Size(min = 1, max = 200, message = "Name must be 1–200 characters")
    private String name;

    @NotBlank(message = "Type is required (goods / service)")
    @Pattern(
            regexp = "(?i)^(goods|service)$",
            message = "Type must be 'goods' or 'service'"
    )
    private String type;

    // ===============================
    // OPTIONAL BASIC INFO
    // ===============================

    @Size(max = 50, message = "SKU max 50 characters")
    private String sku;

    @Size(max = 100, message = "Category max 100 characters")
    private String category;

    @Size(max = 100, message = "Brand max 100 characters")
    private String brand;

    @Size(max = 30, message = "Unit max 30 characters")
    private String unit;

    @Size(max = 500, message = "Description max 500 characters")
    private String description;

    // ===============================
    // PRICES
    // ===============================

    @DecimalMin(value = "0.0", message = "Sale price cannot be negative")
    private BigDecimal salePrice;

    @DecimalMin(value = "0.0", message = "Purchase price cannot be negative")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", message = "MRP price cannot be negative")
    private BigDecimal mrpPrice;

    // ===============================
    // INVENTORY
    // ===============================

    @DecimalMin(value = "0.0", message = "Opening stock cannot be negative")
    private BigDecimal openingStock;

    @Min(value = 0, message = "Low stock alert cannot be negative")
    private Integer lowStockAlert;

    // ===============================
    // TAX
    // ===============================

    @DecimalMin(value = "0.0", message = "GST rate cannot be negative")
    @DecimalMax(value = "100.0", message = "GST rate cannot exceed 100%")
    private BigDecimal gstRate;

    @Size(max = 20, message = "HSN max 20 characters")
    private String hsn;

    // ===============================
    // DEFAULT DISCOUNTS
    // ===============================

    @DecimalMin(value = "0.0", message = "Sale discount % cannot be negative")
    @DecimalMax(value = "100.0", message = "Sale discount % cannot exceed 100")
    private BigDecimal saleDiscountPercent;

    @DecimalMin(value = "0.0", message = "Sale discount amount cannot be negative")
    private BigDecimal saleDiscountAmount;

    @DecimalMin(value = "0.0", message = "Purchase discount % cannot be negative")
    @DecimalMax(value = "100.0", message = "Purchase discount % cannot exceed 100")
    private BigDecimal purchaseDiscountPercent;

    @DecimalMin(value = "0.0", message = "Purchase discount amount cannot be negative")
    private BigDecimal purchaseDiscountAmount;

    // ===============================
    // FLAGS
    // ===============================

    private Boolean isActive;

    private Boolean isFavorite;
}