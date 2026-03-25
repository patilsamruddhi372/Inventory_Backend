package com.Inventory.Inventory_Backend.settings.dto;

import lombok.Data;

@Data
public class BillingDetailsRequestDTO {
    private Long businessId;

    private String billingName;
    private String phone;
    private String billingCountry;
    private String billingAddress;
    private String billingState;
    private String billingCity;
    private String billingZipcode;
    private String paymentMethod;
}
