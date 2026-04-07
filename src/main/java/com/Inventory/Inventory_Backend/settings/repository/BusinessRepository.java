package com.Inventory.Inventory_Backend.settings.repository;

import com.Inventory.Inventory_Backend.settings.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);

    List<Business> findAllByUserId(Long userId);
}
