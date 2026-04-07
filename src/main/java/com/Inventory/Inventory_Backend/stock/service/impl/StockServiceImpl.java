package com.Inventory.Inventory_Backend.stock.service.impl;

import com.Inventory.Inventory_Backend.stock.dto.StockAdjustmentRequestDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockMovementResponseDTO;
import com.Inventory.Inventory_Backend.stock.dto.StockResponseDTO;
import com.Inventory.Inventory_Backend.stock.entity.Stock;
import com.Inventory.Inventory_Backend.stock.entity.StockMovement;
import com.Inventory.Inventory_Backend.stock.repository.StockMovementRepository;
import com.Inventory.Inventory_Backend.stock.repository.StockRepository;
import com.Inventory.Inventory_Backend.stock.service.StockService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;

    // =========================================================
    // INCREASE STOCK (PURCHASE)
    // =========================================================
    @Override
    @Transactional
    public void increaseStock(Long businessId, Long itemId, BigDecimal quantity, Long referenceId) {

        Stock stock = stockRepository
                .findByBusinessIdAndItemId(businessId, itemId)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setBusinessId(businessId);
                    newStock.setItemId(itemId);
                    newStock.setQuantity(BigDecimal.ZERO);
                    newStock.setCreatedAt(LocalDateTime.now());
                    return newStock;
                });

        BigDecimal currentQty = Optional.ofNullable(stock.getQuantity())
                .orElse(BigDecimal.ZERO);

        stock.setQuantity(currentQty.add(quantity));
        stock.setUpdatedAt(LocalDateTime.now());

        stockRepository.save(stock);

        movementRepository.save(
                StockMovement.builder()
                        .businessId(businessId)
                        .itemId(itemId)
                        .quantity(quantity)
                        .movementType("PURCHASE_IN")
                        .referenceType("PURCHASE_INVOICE")
                        .referenceId(referenceId)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // =========================================================
    // DECREASE STOCK (SALE)
    // =========================================================
    @Override
    @Transactional
    public void decreaseStock(Long businessId, Long itemId, BigDecimal quantity, Long referenceId) {

        Stock stock = stockRepository
                .findByBusinessIdAndItemId(businessId, itemId)
                .orElseThrow(() ->
                        new RuntimeException("Stock not found for item: " + itemId));

        BigDecimal currentQty = Optional.ofNullable(stock.getQuantity())
                .orElse(BigDecimal.ZERO);

        if (currentQty.compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient stock for item: " + itemId);
        }

        stock.setQuantity(currentQty.subtract(quantity));
        stock.setUpdatedAt(LocalDateTime.now());

        stockRepository.save(stock);

        movementRepository.save(
                StockMovement.builder()
                        .businessId(businessId)
                        .itemId(itemId)
                        .quantity(quantity.negate())
                        .movementType("SALE_OUT")
                        .referenceType("SALES_INVOICE")
                        .referenceId(referenceId)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // =========================================================
    // DECREASE STOCK FROM QUOTATION CONVERSION
    // =========================================================
    @Override
    @Transactional
    public void decreaseStockFromQuotation(Long businessId,
                                           Long itemId,
                                           BigDecimal quantity,
                                           Long quotationId) {

        decreaseStock(businessId, itemId, quantity, quotationId);
    }

    // =========================================================
    // STOCK ADJUSTMENT
    // =========================================================
    @Override
    @Transactional
    public void adjustStock(Long businessId, StockAdjustmentRequestDTO request) {

        Stock stock = stockRepository
                .findByBusinessIdAndItemId(businessId, request.getItemId())
                .orElseThrow(() ->
                        new RuntimeException("Stock not found for item: " + request.getItemId()));

        BigDecimal currentQty = Optional.ofNullable(stock.getQuantity())
                .orElse(BigDecimal.ZERO);

        BigDecimal newQty = request.getNewQuantity();

        if (newQty == null) {
            throw new RuntimeException("New quantity is required for stock adjustment");
        }

        BigDecimal adjustment = newQty.subtract(currentQty);

        stock.setQuantity(newQty);
        stock.setUpdatedAt(LocalDateTime.now());

        stockRepository.save(stock);

        movementRepository.save(
                StockMovement.builder()
                        .businessId(businessId)
                        .itemId(request.getItemId())
                        .quantity(adjustment)
                        .movementType("ADJUSTMENT")
                        .referenceType("MANUAL_ADJUSTMENT")
                        .remarks(request.getReason())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // =========================================================
    // GET STOCK FOR ITEM
    // =========================================================
    @Override
    public StockResponseDTO getStock(Long businessId, Long itemId) {

        Stock stock = stockRepository
                .findByBusinessIdAndItemId(businessId, itemId)
                .orElse(null);

        if (stock == null){
            return StockResponseDTO.builder()
                    .businessId(businessId)
                    .itemId(itemId)
                    .quantity(BigDecimal.ZERO)
                    .build();
        }

        return StockResponseDTO.builder()
                .businessId(stock.getBusinessId())
                .itemId(stock.getItemId())
                .quantity(
                        stock.getQuantity() != null
                        ? stock.getQuantity()
                        : BigDecimal.ZERO)
                .build();
    }

    // =========================================================
    // GET ALL STOCK
    // =========================================================
    @Override
    public List<StockResponseDTO> getAllStock(Long businessId) {

        return stockRepository
                .findByBusinessId(businessId)
                .stream()
                .map(stock -> StockResponseDTO.builder()
                        .businessId(stock.getBusinessId())
                        .itemId(stock.getItemId())
                        .quantity(stock.getQuantity())
                        .build())
                .toList();
    }

    // =========================================================
    // GET LOW STOCK ITEMS
    // =========================================================
    @Override
    public List<StockResponseDTO> getLowStockItems(Long businessId) {

        return stockRepository
                .findLowStockItems(businessId)
                .stream()
                .map(stock -> StockResponseDTO.builder()
                        .businessId(stock.getBusinessId())
                        .itemId(stock.getItemId())
                        .quantity(stock.getQuantity())
                        .build())
                .toList();
    }

    // =========================================================
    // STOCK MOVEMENT HISTORY
    // =========================================================
    @Override
    public List<StockMovementResponseDTO> getStockMovements(Long businessId, Long itemId) {

        return movementRepository
                .findByBusinessIdAndItemIdOrderByCreatedAtDesc(businessId, itemId)
                .stream()
                .map(m -> StockMovementResponseDTO.builder()
                        .id(m.getId())
                        .businessId(m.getBusinessId())
                        .itemId(m.getItemId())
                        .quantity(m.getQuantity())
                        .movementType(m.getMovementType())
                        .referenceType(m.getReferenceType())
                        .referenceId(m.getReferenceId())
                        .remarks(m.getRemarks())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }
}