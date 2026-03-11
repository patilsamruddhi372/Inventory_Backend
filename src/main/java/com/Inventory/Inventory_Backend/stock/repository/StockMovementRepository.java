package com.Inventory.Inventory_Backend.stock.repository;

import com.Inventory.Inventory_Backend.stock.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {

    // =========================================================
    // GET MOVEMENTS BY ITEM
    // =========================================================
    List<StockMovement> findByItemIdOrderByCreatedAtDesc(Long itemId);

    // =========================================================
    // GET MOVEMENTS BY BUSINESS
    // =========================================================
    List<StockMovement> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    // =========================================================
    // GET MOVEMENTS BY BUSINESS + ITEM
    // =========================================================
    List<StockMovement> findByBusinessIdAndItemIdOrderByCreatedAtDesc(
            Long businessId,
            Long itemId
    );

    // =========================================================
    // CHECK ITEM DEPENDENCY (USED IN ITEM DELETE)
    // =========================================================
    boolean existsByItemIdAndBusinessId(Long itemId, Long businessId);
}