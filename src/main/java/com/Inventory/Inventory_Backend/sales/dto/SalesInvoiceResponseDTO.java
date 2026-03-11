package com.Inventory.Inventory_Backend.sales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesInvoiceResponseDTO {

    private Long id;
    private Long businessId;
    private Long partyId;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    // ==========================================================
    // FINANCIAL
    // ==========================================================

    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalCgst;
    private BigDecimal totalSgst;
    private BigDecimal totalIgst;
    private BigDecimal totalTax;

    // ==========================================================
    // PAYMENT
    // ==========================================================

    private String paymentType;   // ✅ ADDED

    private BigDecimal grandTotal;
    private BigDecimal amountPaid;
    private BigDecimal balance;

    // ==========================================================
    // STATUS
    // ==========================================================

    private String status;

    // ==========================================================
    // EWAY BILL
    // ==========================================================

    private Boolean eWayBillRequired;

    // ==========================================================
    // SYSTEM
    // ==========================================================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==========================================================
    // ITEMS
    // ==========================================================

    private List<SalesInvoiceItemResponseDTO> items;
}