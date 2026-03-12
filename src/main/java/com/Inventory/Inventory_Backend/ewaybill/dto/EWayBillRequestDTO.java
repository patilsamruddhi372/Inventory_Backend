package com.Inventory.Inventory_Backend.ewaybill.dto;

import com.Inventory.Inventory_Backend.ewaybill.entity.TransportMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EWayBillRequestDTO {
    // ==========================================================
    // LINK TO INVOICE
    // ==========================================================

    @NotNull(message = "Sales invoice ID is required")
    private Long salesInvoiceId;

    // ==========================================================
    // TRANSPORT DETAILS
    // ==========================================================

    @NotNull(message = "Transport mode is required")
    private TransportMode transportMode;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Distance is required")
    @Min(value = 1, message = "Distance must be greater than 0")
    private Integer distanceKm;

}
