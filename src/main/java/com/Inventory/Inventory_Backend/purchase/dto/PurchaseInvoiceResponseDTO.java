package com.Inventory.Inventory_Backend.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseInvoiceResponseDTO {

    // =========================================================
    // IDENTIFIERS
    // =========================================================

    private Long id;
    private Long businessId;

    // =========================================================
    // PARTY INFO
    // =========================================================

    private Long partyId;
    private String partyName;

    // =========================================================
    // BILL INFO
    // =========================================================

    private String billNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    // =========================================================
    // FINANCIALS
    // =========================================================

    private BigDecimal subtotal;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;

    // =========================================================
    // PAYMENT
    // =========================================================

    private String paymentType;   // ✅ ADDED

    private BigDecimal amountPaid;
    private BigDecimal balance;

    // =========================================================
    // STATUS
    // =========================================================

    private String status;

    // =========================================================
    // NOTES
    // =========================================================

    private String notes;

    // =========================================================
    // AUDIT
    // =========================================================

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // =========================================================
    // ITEMS
    // =========================================================

    private List<PurchaseInvoiceItemResponseDTO> items;

    private int totalItems;
}