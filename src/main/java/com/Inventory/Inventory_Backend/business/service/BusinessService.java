package com.Inventory.Inventory_Backend.business.service;

import com.Inventory.Inventory_Backend.business.dto.BusinessRequest;
import com.Inventory.Inventory_Backend.settings.entity.Business;

public interface BusinessService {
    Business createBusiness(BusinessRequest request, Long userId);
}
