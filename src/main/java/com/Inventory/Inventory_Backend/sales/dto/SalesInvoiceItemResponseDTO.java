package com.Inventory.Inventory_Backend.sales.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesInvoiceItemResponseDTO {

    // ==========================================================
    // IDENTITY
    // ==========================================================

    private Long id;
    private Long itemId;

    // ==========================================================
    // ITEM DETAILS
    // ==========================================================

    private BigDecimal quantity;
    private String unit;

    // ==========================================================
    // PRICING
    // ==========================================================

    private BigDecimal rate;
    private BigDecimal discount;

    // ==========================================================
    // TAX
    // ==========================================================

    private BigDecimal gstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;

    // ==========================================================
    // TOTAL
    // ==========================================================

    private BigDecimal total;
}