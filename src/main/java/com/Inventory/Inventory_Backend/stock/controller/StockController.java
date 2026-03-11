package com.Inventory.Inventory_Backend.stock.controller;

import com.Inventory.Inventory_Backend.stock.dto.StockAdjustmentRequestDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockMovementResponseDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockResponseDTO;
import com.Inventory.Inventory_Backend.stock.service.StockService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    // =========================================================
    // GET STOCK FOR SINGLE ITEM
    // =========================================================
    @GetMapping("/{itemId}")
    public ResponseEntity<StockResponseDTO> getStock(
            @RequestHeader("X-Business-Id") Long businessId,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                stockService.getStock(businessId, itemId)
        );
    }

    // =========================================================
    // GET ALL STOCK FOR BUSINESS
    // =========================================================
    @GetMapping
    public ResponseEntity<List<StockResponseDTO>> getAllStock(
            @RequestHeader("X-Business-Id") Long businessId
    ) {
        return ResponseEntity.ok(
                stockService.getAllStock(businessId)
        );
    }

    // =========================================================
    // GET LOW STOCK ITEMS ⭐
    // =========================================================
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockResponseDTO>> getLowStockItems(
            @RequestHeader("X-Business-Id") Long businessId
    ) {
        return ResponseEntity.ok(
                stockService.getLowStockItems(businessId)
        );
    }

    // =========================================================
    // GET STOCK MOVEMENT HISTORY
    // =========================================================
    @GetMapping("/{itemId}/movements")
    public ResponseEntity<List<StockMovementResponseDTO>> getMovements(
            @RequestHeader("X-Business-Id") Long businessId,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                stockService.getStockMovements(businessId, itemId)
        );
    }

    // =========================================================
    // MANUAL STOCK ADJUSTMENT
    // =========================================================
    @PostMapping("/adjust")
    public ResponseEntity<String> adjustStock(
            @RequestHeader("X-Business-Id") Long businessId,
            @RequestBody StockAdjustmentRequestDTO request
    ) {
        stockService.adjustStock(businessId, request);
        return ResponseEntity.ok("Stock adjusted successfully");
    }
}