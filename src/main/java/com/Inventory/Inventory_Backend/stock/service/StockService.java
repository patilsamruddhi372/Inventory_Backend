package com.Inventory.Inventory_Backend.stock.service;

import com.Inventory.Inventory_Backend.stock.dto.StockAdjustmentRequestDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockMovementResponseDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface StockService {

    // =========================================================
    // INCREASE STOCK (PURCHASE)
    // =========================================================
    void increaseStock(Long businessId, Long itemId, BigDecimal quantity, Long referenceId);

    // =========================================================
    // DECREASE STOCK (SALE)
    // =========================================================
    void decreaseStock(Long businessId, Long itemId, BigDecimal quantity, Long referenceId);

    // =========================================================
    // DECREASE STOCK FROM QUOTATION CONVERSION
    // =========================================================
    void decreaseStockFromQuotation(Long businessId, Long itemId, BigDecimal quantity, Long quotationId);

    // =========================================================
    // MANUAL STOCK ADJUSTMENT
    // =========================================================
    void adjustStock(Long businessId, StockAdjustmentRequestDTO request);

    // =========================================================
    // GET STOCK FOR SINGLE ITEM
    // =========================================================
    StockResponseDTO getStock(Long businessId, Long itemId);

    // =========================================================
    // GET ALL STOCK FOR BUSINESS
    // =========================================================
    List<StockResponseDTO> getAllStock(Long businessId);

    // =========================================================
    // GET LOW STOCK ITEMS ⭐
    // =========================================================
    List<StockResponseDTO> getLowStockItems(Long businessId);

    // =========================================================
    // GET STOCK MOVEMENT HISTORY
    // =========================================================
    List<StockMovementResponseDTO> getStockMovements(Long businessId, Long itemId);
}