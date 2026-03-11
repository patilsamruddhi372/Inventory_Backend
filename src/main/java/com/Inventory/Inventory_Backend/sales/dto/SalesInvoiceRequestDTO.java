package com.Inventory.Inventory_Backend.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesInvoiceRequestDTO {

    // ==========================================================
    // CUSTOMER
    // ==========================================================

    @NotNull(message = "Customer (party) ID is required")
    private Long partyId;

    // ==========================================================
    // INVOICE INFO
    // ==========================================================

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    private LocalDate dueDate;

    // ==========================================================
    // PAYMENT
    // ==========================================================

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @Builder.Default
    @DecimalMin(value = "0.00", message = "Amount paid cannot be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    /**
     * true  → IGST applied (inter-state sale)
     * false → CGST + SGST applied (intra-state sale)
     */
    @Builder.Default
    private boolean interState = false;


    // ==========================================================
    // ITEMS
    // ==========================================================

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<SalesInvoiceItemDTO> items;


}