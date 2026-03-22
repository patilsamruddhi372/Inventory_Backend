package com.Inventory.Inventory_Backend.party.service;

import com.Inventory.Inventory_Backend.party.dto.PartyCreateRequest;
import com.Inventory.Inventory_Backend.party.dto.PartyResponse;
import com.Inventory.Inventory_Backend.party.dto.PartyUpdateRequest;
import com.Inventory.Inventory_Backend.party.entity.PartyType; // ✅ added

import java.util.List;

public interface PartyService {

    // -------- Create new party --------
    PartyResponse create(Long businessId, PartyCreateRequest request);

    // -------- Get all parties for a business --------
    List<PartyResponse> getAll(Long businessId);

    // -------- Get single party by ID --------
    PartyResponse getById(Long businessId, Long partyId);

    // -------- Full update (PUT) --------
    PartyResponse update(Long businessId, Long partyId, PartyUpdateRequest request);

    // -------- Partial update (PATCH) --------
    PartyResponse patchUpdate(Long businessId, Long partyId, PartyUpdateRequest request);

    // -------- Soft delete --------
    void delete(Long businessId, Long partyId);

    // ================= NEW METHODS =================

    // ✅ Get parties by single type (CUSTOMER / SUPPLIER)
    List<PartyResponse> getByType(Long businessId, PartyType type);

    // 🔥 (PRO) Get parties by multiple types (CUSTOMER + BOTH, etc.)
    List<PartyResponse> getByTypes(Long businessId, List<PartyType> types);
}