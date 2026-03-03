package com.Inventory.Inventory_Backend.item.service;

import com.Inventory.Inventory_Backend.item.dto.ItemMapper;
import com.Inventory.Inventory_Backend.item.dto.ItemRequestDTO;
//import com.Inventory.Inventory_Backend.item.dto.itemResponseDTO;
import com.Inventory.Inventory_Backend.item.dto.itemResponceDTO;
import com.Inventory.Inventory_Backend.item.entity.Item;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    // TODO: replace with actual auth / tenant resolver
    private Long getCurrentBusinessId() {
        return 1L;
    }

    // ─── READ ALL ─────────────────────────────────
    @Transactional(readOnly = true)
    public List<itemResponceDTO> getAll() {
        List<Item> items = repository.findByBusinessIdAndIsActiveTrue(
                getCurrentBusinessId()
        );
        return mapper.toResponceList(items);
    }

    // ─── READ ONE ─────────────────────────────────
    @Transactional(readOnly = true)
    public itemResponceDTO getById(Long id) {
        Item item = findItemOrThrow(id);
        return mapper.toResponce(item);
    }

    // ─── CREATE ───────────────────────────────────
    public itemResponceDTO create(ItemRequestDTO dto) {
        Item entity = mapper.toEntity(dto);
        entity.setBusinessId(getCurrentBusinessId());

        // service type → stock = 0
        if ("service".equalsIgnoreCase(entity.getType())) {
            entity.setStock(0);
            entity.setLowStockAlert(0);
        }

        Item saved = repository.save(entity);
        return mapper.toResponce(saved);
    }

    // ─── UPDATE ───────────────────────────────────
    public itemResponceDTO update(Long id, ItemRequestDTO dto) {
        Item existing = findItemOrThrow(id);
        mapper.updateEntity(existing, dto);

        // service type → stock = 0
        if ("service".equalsIgnoreCase(existing.getType())) {
            existing.setStock(0);
            existing.setLowStockAlert(0);
        }

        Item saved = repository.save(existing);
        return mapper.toResponce(saved);
    }

    // ─── DELETE (soft) ────────────────────────────
    public void delete(Long id) {
        Item item = findItemOrThrow(id);
        item.setIsActive(false);
        repository.save(item);
    }

    // ─── TOGGLE FAVORITE ──────────────────────────
    public void toggleFavorite(Long id) {
        int updated = repository.toggleFavorite(id, getCurrentBusinessId());
        if (updated == 0) {
            throw new EntityNotFoundException("Item not found: " + id);
        }
    }

    // ─── BULK DELETE (soft) ───────────────────────
    public void bulkDelete(Set<Long> ids) {
        Long businessId = getCurrentBusinessId();
        List<Item> items = repository.findAllById(ids).stream()
                .filter(i -> businessId.equals(i.getBusinessId()))
                .toList();

        if (items.isEmpty()) {
            throw new EntityNotFoundException("No items found for given IDs");
        }

        items.forEach(i -> i.setIsActive(false));
        repository.saveAll(items);
    }

    // ─── HELPER ───────────────────────────────────
    private Item findItemOrThrow(Long id) {
        return repository.findByIdAndBusinessId(id, getCurrentBusinessId())
                .filter(Item::getIsActive)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found: " + id)
                );
    }
}