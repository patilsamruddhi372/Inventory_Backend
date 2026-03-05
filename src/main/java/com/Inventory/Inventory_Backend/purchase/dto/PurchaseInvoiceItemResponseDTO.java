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

    private Long id;

    private Long itemId;

    private String itemName;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal rate;

    private BigDecimal gstRate;

    // calculated tax for this line item
    private BigDecimal taxAmount;

    // total including tax
    private BigDecimal total;
}