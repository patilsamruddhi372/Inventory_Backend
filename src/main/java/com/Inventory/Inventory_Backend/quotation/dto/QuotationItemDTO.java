package com.Inventory.Inventory_Backend.quotation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QuotationItemDTO {

    private Long itemId;

    private String itemName;

    private String description;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal rate;

    private BigDecimal discountPercent;

    private BigDecimal discountAmount;

    private BigDecimal gstRate;

    private BigDecimal taxAmount;

    private BigDecimal amount;

    private String hsnCode;
}