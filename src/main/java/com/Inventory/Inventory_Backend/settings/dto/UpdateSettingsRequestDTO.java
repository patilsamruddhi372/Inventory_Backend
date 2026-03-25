package com.Inventory.Inventory_Backend.settings.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSettingsRequestDTO {
    private String businessName;
    private String gstin;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private Boolean gstEnabled;
    private Boolean stockEnabled;
}
