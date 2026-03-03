package com.Inventory.Inventory_Backend.common;

import org.springframework.stereotype.Component;

@Component
public class context {

    // Temporary: always return default business
    public Long getCurrentBusinessId() {
        return 1L;
    }
}