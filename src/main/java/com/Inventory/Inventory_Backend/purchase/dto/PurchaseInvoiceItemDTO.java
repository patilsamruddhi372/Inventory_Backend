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

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
    @Digits(integer = 12, fraction = 3, message = "Quantity format is invalid")
    private BigDecimal quantity;

    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.00", message = "Rate must be zero or positive")
    @Digits(integer = 13, fraction = 2, message = "Rate format is invalid")
    private BigDecimal rate;

    // GST rate in percentage (0 - 100)
    @DecimalMin(value = "0.00", message = "GST rate must be zero or positive")
    @DecimalMax(value = "100.00", message = "GST rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "GST rate format is invalid")
    @Builder.Default
    private BigDecimal gstRate = BigDecimal.ZERO;
}