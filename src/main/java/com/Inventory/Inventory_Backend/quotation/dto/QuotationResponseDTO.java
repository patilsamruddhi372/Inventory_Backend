package com.Inventory.Inventory_Backend.quotation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class QuotationResponseDTO {

    private Long id;

    private String quotationNumber;

    private Long partyId;

    private LocalDate quotationDate;

    private LocalDate validUntil;

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal shippingCharges;

    private BigDecimal totalAmount;

    private String paymentTerms;

    private String deliveryTime;

    private String notes;

    private String status;

    private List<QuotationItemDTO> items;
}