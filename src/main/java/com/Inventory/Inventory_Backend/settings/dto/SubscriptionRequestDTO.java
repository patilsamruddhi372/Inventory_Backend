package com.Inventory.Inventory_Backend.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {
    private Long businessId;

    private String plan;
    private String billingCycle;

    private String billingName;
    private String phone;
    private String country;
    private String streetAddress;
    private String state;
    private String city;
    private String zip;

    private Boolean gstRegistered;
    private String gstin;

    private String paymentType;




}
