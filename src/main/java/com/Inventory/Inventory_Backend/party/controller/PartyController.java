package com.Inventory.Inventory_Backend.party.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.party.dto.PartyCreateRequest;
import com.Inventory.Inventory_Backend.party.dto.PartyResponse;
import com.Inventory.Inventory_Backend.party.dto.PartyUpdateRequest;
import com.Inventory.Inventory_Backend.party.service.PartyServiceImpl;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyServiceImpl service;
    private final BusinessContext businessContext;

    public PartyController(PartyServiceImpl service,
                           BusinessContext businessContext) {
        this.service = service;
        this.businessContext = businessContext;
    }

    // -------- CREATE --------
    @PostMapping
    public ResponseEntity<PartyResponse> create(
            @Valid @RequestBody PartyCreateRequest request) {

        Long businessId = businessContext.getCurrentBusinessId();
        return ResponseEntity.ok(service.create(businessId, request));
    }

    // -------- GET ALL --------
    @GetMapping
    public ResponseEntity<List<PartyResponse>> getAll() {

        Long businessId = businessContext.getCurrentBusinessId();
        return ResponseEntity.ok(service.getAll(businessId));
    }

    // -------- GET BY ID --------
    @GetMapping("/{id}")
    public ResponseEntity<PartyResponse> getById(
            @PathVariable Long id) {

        Long businessId = businessContext.getCurrentBusinessId();
        return ResponseEntity.ok(service.getById(businessId, id));
    }

    // -------- FULL UPDATE --------
    @PutMapping("/{id}")
    public ResponseEntity<PartyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PartyUpdateRequest request) {

        Long businessId = businessContext.getCurrentBusinessId();
        return ResponseEntity.ok(service.update(businessId, id, request));
    }

    // -------- PARTIAL UPDATE (PATCH) --------
    @PatchMapping("/{id}")
    public ResponseEntity<PartyResponse> patchUpdate(
            @PathVariable Long id,
            @RequestBody PartyUpdateRequest request) {

        Long businessId = businessContext.getCurrentBusinessId();
        return ResponseEntity.ok(service.patchUpdate(businessId, id, request));
    }

    // -------- DELETE --------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        Long businessId = businessContext.getCurrentBusinessId();
        service.delete(businessId, id);
        return ResponseEntity.noContent().build();
    }
}