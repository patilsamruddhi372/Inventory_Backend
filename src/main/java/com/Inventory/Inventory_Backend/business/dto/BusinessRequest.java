package com.Inventory.Inventory_Backend.business.dto;

import lombok.Data;

@Data
public class BusinessRequest {

    private String name;
    private String businessType;
    private String industry;

    private Boolean gstRegistered;
    private String gstNumber;

    private String address;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private String financialYear;
    private Boolean enableStockManagement;
}
