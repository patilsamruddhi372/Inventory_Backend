package com.Inventory.Inventory_Backend.settings.controller;

import com.Inventory.Inventory_Backend.settings.dto.BillingDetailsRequestDTO;
import com.Inventory.Inventory_Backend.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BillingController {
    private final SettingsService settingsService;

    //Save Billing details
    @PostMapping
    public String saveBilling(@RequestBody BillingDetailsRequestDTO request) {
        settingsService.saveBillingDetails(request);
        return "Billing details saved successfully";
    }

    //Get Billing details
    @GetMapping("/{businessId}")
    public BillingDetailsRequestDTO getBilling(@PathVariable Long businessId) {
        return settingsService.getBillingDetails(businessId);
    }
}
