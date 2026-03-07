package com.Inventory.Inventory_Backend.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseInvoiceItemDTO {

    // =========================================================
    // ITEM
    // =========================================================

    @NotNull(message = "Item ID is required")
    private Long itemId;

    // =========================================================
    // QUANTITY
    // =========================================================

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", inclusive = true,
            message = "Quantity must be greater than zero")
    @Digits(integer = 12, fraction = 3,
            message = "Quantity format is invalid")
    private BigDecimal quantity;

    // =========================================================
    // UNIT
    // =========================================================

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    // =========================================================
    // RATE
    // =========================================================

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.00",
            message = "Rate must be zero or positive")
    @Digits(integer = 13, fraction = 2,
            message = "Rate format is invalid")
    private BigDecimal rate;

    // =========================================================
    // GST RATE
    // =========================================================

    @DecimalMin(value = "0.00",
            message = "GST rate must be zero or positive")
    @DecimalMax(value = "100.00",
            message = "GST rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2,
            message = "GST rate format is invalid")
    @Builder.Default
    private BigDecimal gstRate = BigDecimal.ZERO;
}