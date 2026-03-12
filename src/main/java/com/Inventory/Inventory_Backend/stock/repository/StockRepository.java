package com.Inventory.Inventory_Backend.stock.repository;

import com.Inventory.Inventory_Backend.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // =========================================================
    // FIND STOCK FOR ITEM IN BUSINESS
    // =========================================================
    Optional<Stock> findByBusinessIdAndItemId(Long businessId, Long itemId);

    // =========================================================
    // GET ALL STOCK FOR BUSINESS
    // =========================================================
    List<Stock> findByBusinessId(Long businessId);

    // =========================================================
    // GET LOW STOCK ITEMS
    // =========================================================
    @Query("""
        SELECT s
        FROM Stock s
        JOIN Item i ON s.itemId = i.id
        WHERE s.businessId = :businessId
        AND i.lowStockAlert > 0
        AND s.quantity <= i.lowStockAlert
    """)
    List<Stock> findLowStockItems(Long businessId);
}