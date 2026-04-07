package com.Inventory.Inventory_Backend.settings.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsResponseDTO {
    private String businessName;
    private String gstin;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private Boolean gstEnabled;
    private Boolean stockEnabled;

    // Subscription info
    private String plan;
    private LocalDate validFrom;
    private LocalDate validTill;
    private Long daysRemaining;

    private Long id;
}
