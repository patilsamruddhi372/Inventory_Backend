package com.Inventory.Inventory_Backend.item.controller;

import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import com.Inventory.Inventory_Backend.item.dto.ItemRequestDTO;
import com.Inventory.Inventory_Backend.item.dto.ItemResponseDTO;
import com.Inventory.Inventory_Backend.item.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JwtUtil jwtUtil;

    private final ItemService itemService;

    // =========================
    // GET ALL ITEMS
    // =========================
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<ItemResponseDTO> items = itemService.getAll();
        return ResponseEntity.ok(items);
    }

    // =========================
    // GET ITEM BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long id) {
        ItemResponseDTO item = itemService.getById(id);
        return ResponseEntity.ok(item);
    }

    // =========================
    // CREATE ITEM
    // =========================
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(
            @Valid @RequestBody ItemRequestDTO dto) {

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
            @Valid @RequestBody ItemRequestDTO dto) {

        ItemResponseDTO updatedItem = itemService.update(id, dto);
        return ResponseEntity.ok(updatedItem);
    }

    // =========================
    // DELETE ITEM (SOFT DELETE)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, String>> deleteItem(@PathVariable Long id) {
        itemService.delete(id);

        // This creates valid JSON: {"message": "Item deleted successfully"}
        return ResponseEntity.ok(java.util.Map.of("message", "Item deleted successfully"));
    }

    // =========================
    // TOGGLE FAVORITE
    // =========================
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id) {

        itemService.toggleFavorite(id);

        return ResponseEntity.noContent().build();
    }

    // =========================
    // BULK DELETE
    // =========================
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody Set<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        itemService.bulkDelete(ids);

        return ResponseEntity.noContent().build();
    }
}