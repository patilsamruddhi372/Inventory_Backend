package com.Inventory.Inventory_Backend.settings.controller;

import com.Inventory.Inventory_Backend.settings.dto.SettingsResponseDTO;
import com.Inventory.Inventory_Backend.settings.dto.UpdateSettingsRequestDTO;
import com.Inventory.Inventory_Backend.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SettingsController {
    private final SettingsService settingsService;

    //get Settings
    @GetMapping("{businessId}")
    public SettingsResponseDTO getSettings(@PathVariable Long businessId){
        return settingsService.getSettings(businessId);
    }

    //update Settings
    @PutMapping("/{businessId}")
    public String updateSettings(@PathVariable Long businessId,
                                 @RequestBody UpdateSettingsRequestDTO request){
        settingsService.updateSettings(businessId, request);
        return "Settings updated successfully";
    }
}
