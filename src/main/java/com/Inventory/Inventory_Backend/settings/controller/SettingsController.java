package com.Inventory.Inventory_Backend.settings.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.settings.dto.SettingsResponseDTO;
import com.Inventory.Inventory_Backend.settings.dto.UpdateSettingsRequestDTO;
import com.Inventory.Inventory_Backend.settings.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/settings")
@CrossOrigin("*")
public class SettingsController {

    private final SettingsService settingsService;
    private final BusinessContext businessContext;

    // ✅ ADD THIS CONSTRUCTOR (VERY IMPORTANT)
    public SettingsController(SettingsService settingsService,
                              BusinessContext businessContext) {
        this.settingsService = settingsService;
        this.businessContext = businessContext;
    }

    // ✅ GET Settings (NO businessId in URL)
    @GetMapping
    public SettingsResponseDTO getSettings() {
        Long businessId = businessContext.getBusinessId();
        return settingsService.getSettings(businessId);

        /*String token = authHeader.substring(7); // remove Bearer
        Long userId = settingsService.extractUserIdFromToken(token);

        return settingsService.getSettingsByUserId(userId);*/
    }

    // ✅ UPDATE Settings
    @PutMapping
    public String updateSettings(@RequestBody UpdateSettingsRequestDTO request) {
        Long businessId = businessContext.getBusinessId();
        settingsService.updateSettings(businessId, request);
        return "Settings updated successfully";
    }
}