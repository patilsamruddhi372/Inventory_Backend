package com.Inventory.Inventory_Backend.item.controller;

import com.Inventory.Inventory_Backend.item.dto.ItemRequestDTO;
import com.Inventory.Inventory_Backend.item.dto.itemResponceDTO;
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

    private final ItemService service;

    // ─── READ ALL ─────────────────────────────────
    @GetMapping
    public ResponseEntity<List<itemResponceDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());              // ✅ JUST THIS. Nothing else.
    }

    // ─── READ ONE ─────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<itemResponceDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ─── CREATE ───────────────────────────────────
    @PostMapping
    public ResponseEntity<itemResponceDTO> create(
            @Valid @RequestBody ItemRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    // ─── UPDATE ───────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<itemResponceDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // ─── DELETE (soft) ────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─── TOGGLE FAVORITE ──────────────────────────
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long id) {
        service.toggleFavorite(id);
        return ResponseEntity.ok().build();
    }

    // ─── BULK DELETE ──────────────────────────────
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody Set<Long> ids) {
        service.bulkDelete(ids);
        return ResponseEntity.noContent().build();
    }
}