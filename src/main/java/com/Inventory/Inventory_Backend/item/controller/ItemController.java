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
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {

        itemService.delete(id);

        return ResponseEntity.ok("Item deleted successfully");
    }

    // =========================
    // TOGGLE FAVORITE
    // =========================
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long id) {

        itemService.toggleFavorite(id);

        return ResponseEntity.ok("Item favorite status updated");
    }

    // =========================
    // BULK DELETE
    // =========================
    @DeleteMapping("/bulk")
    public ResponseEntity<String> bulkDelete(@RequestBody Set<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("No item IDs provided");
        }

        itemService.bulkDelete(ids);

        return ResponseEntity.ok("Items deleted successfully");
    }
}