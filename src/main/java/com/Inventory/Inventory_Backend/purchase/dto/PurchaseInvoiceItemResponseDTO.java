package com.Inventory.Inventory_Backend.purchase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseInvoiceItemResponseDTO {

    // =========================================================
    // IDENTIFIERS
    // =========================================================

    private Long id;
    private Long itemId;

    // =========================================================
    // ITEM DETAILS
    // =========================================================

    private String itemName;

    // =========================================================
    // QUANTITY & UNIT
    // =========================================================

    private BigDecimal quantity;
    private String unit;

    // =========================================================
    // PRICE
    // =========================================================

    private BigDecimal rate;

    // =========================================================
    // TAX
    // =========================================================

    private BigDecimal gstRate;

    // calculated tax amount for this line
    private BigDecimal taxAmount;

    // =========================================================
    // TOTAL
    // =========================================================

    // total = base + tax
    private BigDecimal total;
}