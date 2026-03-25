package com.Inventory.Inventory_Backend.settings.repository;

import com.Inventory.Inventory_Backend.settings.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {
}
