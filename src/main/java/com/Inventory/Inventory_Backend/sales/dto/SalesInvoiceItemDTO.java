package com.Inventory.Inventory_Backend.sales.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesInvoiceItemDTO {

    // ==========================================================
    // ITEM
    // ==========================================================

    @NotNull(message = "Item ID is required")
    private Long itemId;

    // ==========================================================
    // QUANTITY
    // ==========================================================

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
    @Digits(integer = 12, fraction = 3)
    private BigDecimal quantity;

    // ==========================================================
    // UNIT
    // ==========================================================

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    // ==========================================================
    // PRICE
    // ==========================================================

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.01", message = "Rate must be greater than zero")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal rate;

    // ==========================================================
    // DISCOUNT
    // ==========================================================

    @Builder.Default
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    // ==========================================================
    // GST
    // ==========================================================

    @Builder.Default
    @DecimalMin(value = "0.00", message = "GST rate cannot be negative")
    @DecimalMax(value = "100.00", message = "GST rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal gstRate = BigDecimal.ZERO;
}