package com.Inventory.Inventory_Backend.settings.controller;

import com.Inventory.Inventory_Backend.settings.dto.SubscriptionRequestDTO;
import com.Inventory.Inventory_Backend.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SubscriptionController {
    private final SettingsService settingsService;

    // Create subscription (from checkout page)
     @PostMapping("/subscribe")
     public String createSubscription(@RequestBody SubscriptionRequestDTO request) {
         settingsService.createSubscription(request);
         return "Subscription successful";
     }
}
