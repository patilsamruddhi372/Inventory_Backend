package com.Inventory.Inventory_Backend.stock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentRequestDTO {

    // Item whose stock we want to adjust
    @NotNull(message = "Item ID is required")
    private Long itemId;

    // Final quantity after adjustment
    @NotNull(message = "New quantity is required")
    private BigDecimal newQuantity;

    // Reason for adjustment (damaged, lost, correction, etc.)
    private String reason;
}