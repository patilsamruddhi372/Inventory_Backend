package com.Inventory.Inventory_Backend.purchase.dto;

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
public class PurchaseInvoiceRequestDTO {

    @NotNull(message = "Party ID is required")
    private Long partyId;

    @NotBlank(message = "Bill number is required")
    @Size(max = 50, message = "Bill number must not exceed 50 characters")
    private String billNumber;

    @NotNull(message = "Bill date is required")
    private LocalDate billDate;

    private LocalDate dueDate;

    @DecimalMin(value = "0.00", message = "Amount paid must be zero or positive")
    @Digits(integer = 13, fraction = 2, message = "Amount paid format is invalid")
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    @NotNull(message = "At least one item is required")
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<PurchaseInvoiceItemDTO> items;
}