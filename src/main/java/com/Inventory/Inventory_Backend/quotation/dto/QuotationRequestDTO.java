package com.Inventory.Inventory_Backend.quotation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class QuotationRequestDTO {

    private Long partyId;

    private LocalDate quotationDate;

    private LocalDate validUntil;

    private BigDecimal discountAmount;

    private BigDecimal shippingCharges;

    private String notes;

    private String paymentTerms;

    private String deliveryTime;

    private String termsAndConditions;

    private List<QuotationItemDTO> items;
}