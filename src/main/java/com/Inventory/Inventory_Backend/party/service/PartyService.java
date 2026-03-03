package com.Inventory.Inventory_Backend.party.service;

import com.Inventory.Inventory_Backend.party.dto.PartyCreateRequest;
import com.Inventory.Inventory_Backend.party.dto.PartyResponse;
import com.Inventory.Inventory_Backend.party.dto.PartyUpdateRequest;

import java.util.List;

public interface PartyService {

    PartyResponse create(Long businessId, PartyCreateRequest request);

    List<PartyResponse> getAll(Long businessId);

    PartyResponse getById(Long businessId, Long id);

    PartyResponse update(Long businessId, Long id, PartyUpdateRequest request);

    void delete(Long businessId, Long id);
}