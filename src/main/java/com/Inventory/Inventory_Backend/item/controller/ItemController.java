package com.Inventory.Inventory_Backend.item.controller;

import com.Inventory.Inventory_Backend.item.dto.ItemRequestDTO;
import com.Inventory.Inventory_Backend.item.dto.ItemResponseDTO;
import com.Inventory.Inventory_Backend.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    // =========================
    // GET ALL ITEMS
    // =========================
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAll());
    }

    // =========================
    // GET ITEM BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    // =========================
    // CREATE ITEM
    // =========================
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(
            @Valid @RequestBody ItemRequestDTO dto
    ) {
        ItemResponseDTO createdItem = itemService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdItem);
    }

    // =========================
    // UPDATE ITEM
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDTO dto
    ) {
        return ResponseEntity.ok(itemService.update(id, dto));
    }

    // =========================
    // DELETE ITEM (SOFT DELETE)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id
    ) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // TOGGLE FAVORITE
    // =========================
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long id
    ) {
        itemService.toggleFavorite(id);
        return ResponseEntity.ok().build();
    }

    // =========================
    // BULK DELETE
    // =========================
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(
            @RequestBody Set<Long> ids
    ) {
        itemService.bulkDelete(ids);
        return ResponseEntity.noContent().build();
    }
}