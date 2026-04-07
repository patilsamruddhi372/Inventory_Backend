package com.Inventory.Inventory_Backend.settings.service;

import com.Inventory.Inventory_Backend.settings.dto.BillingDetailsRequestDTO;
import com.Inventory.Inventory_Backend.settings.dto.SettingsResponseDTO;
import com.Inventory.Inventory_Backend.settings.dto.SubscriptionRequestDTO;
import com.Inventory.Inventory_Backend.settings.dto.UpdateSettingsRequestDTO;

public interface SettingsService {

    SettingsResponseDTO getSettings(Long businessId);
    BillingDetailsRequestDTO getBillingDetails(Long businessId);

    SettingsResponseDTO getSettingsByUserId(Long userId);

    void updateSettings(Long businessId, UpdateSettingsRequestDTO request);
    void createSubscription(SubscriptionRequestDTO request);
    void saveBillingDetails(BillingDetailsRequestDTO request);

    Long extractUserIdFromToken(String token);
}
