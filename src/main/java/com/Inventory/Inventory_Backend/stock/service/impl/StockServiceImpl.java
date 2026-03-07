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
                .orElse(null);

        if (stock == null) {

            stock = Stock.builder()
                    .businessId(businessId)
                    .itemId(itemId)
                    .quantity(quantity)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

        } else {

            stock.setQuantity(stock.getQuantity().add(quantity));
            stock.setUpdatedAt(LocalDateTime.now());
        }

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

        if (stock.getQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient stock for item: " + itemId);
        }

        stock.setQuantity(stock.getQuantity().subtract(quantity));
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
    // STOCK ADJUSTMENT
    // =========================================================
    @Override
    @Transactional
    public void adjustStock(Long businessId, StockAdjustmentRequestDTO request) {

        Stock stock = stockRepository
                .findByBusinessIdAndItemId(businessId, request.getItemId())
                .orElseThrow(() ->
                        new RuntimeException("Stock not found for item: " + request.getItemId()));

        BigDecimal currentQty = stock.getQuantity();
        BigDecimal newQty = request.getNewQuantity();

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

        if (stock == null) return null;

        return StockResponseDTO.builder()
                .businessId(stock.getBusinessId())
                .itemId(stock.getItemId())
                .quantity(stock.getQuantity())
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