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

    private Long id;

    // Tenant reference (safe to expose in response)
    private Long businessId;

    private Long partyId;
    private String partyName;

    private String billNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private BigDecimal subtotal;
    private BigDecimal totalTax;
    private BigDecimal grandTotal;

    private BigDecimal amountPaid;
    private BigDecimal balance;

    private String status;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<PurchaseInvoiceItemResponseDTO> items;

    private int totalItems;
}