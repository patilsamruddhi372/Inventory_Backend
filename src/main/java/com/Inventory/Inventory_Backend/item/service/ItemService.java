package com.Inventory.Inventory_Backend.item.service;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.item.entity.Item;
import com.Inventory.Inventory_Backend.item.dto.ItemMapper;
import com.Inventory.Inventory_Backend.item.dto.ItemRequestDTO;
import com.Inventory.Inventory_Backend.item.dto.ItemResponseDTO;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;

import com.Inventory.Inventory_Backend.stock.entity.Stock;
import com.Inventory.Inventory_Backend.stock.entity.StockMovement;
import com.Inventory.Inventory_Backend.stock.repository.StockRepository;
import com.Inventory.Inventory_Backend.stock.repository.StockMovementRepository;

import com.Inventory.Inventory_Backend.purchase.repository.PurchaseInvoiceItemRepository;
import com.Inventory.Inventory_Backend.sales.repository.SalesInvoiceItemRepository;
import com.Inventory.Inventory_Backend.quotation.repository.QuotationItemRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    private final PurchaseInvoiceItemRepository purchaseItemRepository;
    private final SalesInvoiceItemRepository salesItemRepository;
    private final QuotationItemRepository quotationItemRepository;

    private final BusinessContext businessContext;

    private Long getCurrentBusinessId(){
        Long businessId = businessContext.getBusinessId();

        if(businessId == null){
            throw new RuntimeException("Business ID not found in context");
        }

        return businessId;
    }

    // =========================
    // GET ALL ITEMS
    // =========================
    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getAll() {

        List<Item> items =
                repository.findByBusinessIdAndIsActiveTrue(getCurrentBusinessId());

        return mapper.toResponseList(items);
    }

    // =========================
    // GET ITEM BY ID
    // =========================
    @Transactional(readOnly = true)
    public ItemResponseDTO getById(Long id) {

        Item item = findItemOrThrow(id);

        return mapper.toResponse(item);
    }

    // =========================
    // CREATE ITEM
    // =========================
    public ItemResponseDTO create(ItemRequestDTO dto) {

        Long businessId = getCurrentBusinessId();

        //  Prevent duplicate item names
        if (repository.existsByNameIgnoreCaseAndBusinessIdAndIsActiveTrue(
                dto.getName().trim(), businessId)) {

            throw new RuntimeException(
                    "Item already exists with name: " + dto.getName()
            );
        }

        if ("service".equalsIgnoreCase(dto.getType())
                && dto.getOpeningStock() != null
                && dto.getOpeningStock().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Opening stock is not applicable for service items");
        }

        Item entity = mapper.toEntity(dto);
        entity.setBusinessId(businessId);
        entity.setName(dto.getName().trim());

        Item savedItem = repository.save(entity);

        // Opening stock
        BigDecimal openingStock =
                dto.getOpeningStock() != null
                        ? dto.getOpeningStock()
                        : BigDecimal.ZERO;

        if("goods".equalsIgnoreCase(savedItem.getType())){
            // 1️⃣ Create stock snapshot
            Stock stock = new Stock();

            stock.setBusinessId(businessId);
            stock.setItemId(savedItem.getId());
            stock.setQuantity(openingStock);
            stock.setCreatedAt(LocalDateTime.now());
            stock.setUpdatedAt(LocalDateTime.now());

            stockRepository.save(stock);

            // 2️⃣ Create stock movement
            if (openingStock.compareTo(BigDecimal.ZERO) > 0) {

                StockMovement movement = new StockMovement();

                movement.setBusinessId(businessId);
                movement.setItemId(savedItem.getId());
                movement.setQuantity(openingStock);
                movement.setMovementType("OPENING_STOCK");
                movement.setReferenceType("ITEM");
                movement.setReferenceId(savedItem.getId());
                movement.setCreatedAt(LocalDateTime.now());

                stockMovementRepository.save(movement);
            }

        }


        return mapper.toResponse(savedItem);
    }

    // =========================
    // UPDATE ITEM
    // =========================
    public ItemResponseDTO update(Long id, ItemRequestDTO dto) {

        Item existing = findItemOrThrow(id);

        mapper.updateEntity(existing, dto);

        Item saved = repository.save(existing);

        return mapper.toResponse(saved);
    }

    // =========================
    // DELETE ITEM (SOFT DELETE)
    // =========================
    public void delete(Long id) {

        Item item = findItemOrThrow(id);

        // Soft delete (Hide the item from the frontend)
        // Since it is just hidden and not permanently deleted, it is safe
        // to do this even if there are past stock movements or sales invoices!
        item.setIsActive(false);

        repository.save(item);
    }

    // =========================
    // TOGGLE FAVORITE
    // =========================
    public void toggleFavorite(Long id) {

        int updated =
                repository.toggleFavorite(id, getCurrentBusinessId());

        if (updated == 0) {
            throw new EntityNotFoundException("Item not found: " + id);
        }
    }

    // =========================
    // BULK DELETE
    // =========================
    public void bulkDelete(Set<Long> ids) {

        Long businessId = getCurrentBusinessId();

        List<Item> items =
                repository.findAllById(ids)
                        .stream()
                        .filter(i -> businessId.equals(i.getBusinessId()))
                        .toList();

        if (items.isEmpty()) {
            throw new EntityNotFoundException("No items found for given IDs");
        }

        items.forEach(i -> i.setIsActive(false));

        repository.saveAll(items);
    }

    // =========================
    // HELPER
    // =========================
    private Item findItemOrThrow(Long id) {

        return repository
                .findByIdAndBusinessId(id, getCurrentBusinessId())
                .filter(Item::getIsActive)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found: " + id)
                );
    }
}