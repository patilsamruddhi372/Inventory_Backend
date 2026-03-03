package com.Inventory.Inventory_Backend.party.service;

import com.Inventory.Inventory_Backend.party.dto.PartyCreateRequest;
import com.Inventory.Inventory_Backend.party.dto.PartyResponse;
import com.Inventory.Inventory_Backend.party.dto.PartyUpdateRequest;
import com.Inventory.Inventory_Backend.party.entity.Party;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyServiceImpl implements PartyService {

    private final PartyRepository repository;

    public PartyServiceImpl(PartyRepository repository) {
        this.repository = repository;
    }

    @Override
    public PartyResponse create(Long businessId, PartyCreateRequest request) {
        Party party = new Party();
        party.setBusinessId(businessId);   // default or context-based business
        party.setName(request.getName());
        party.setType(request.getType());
        party.setGstin(request.getGstin());
        party.setSinceDate(request.getSinceDate());
        party.setCreditLimit(request.getCreditLimit());
        party.setPhone(request.getPhone());
        party.setEmail(request.getEmail());
        party.setAddressLine1(request.getAddressLine1());
        party.setCity(request.getCity());
        party.setState(request.getState());
        party.setPincode(request.getPincode());
        party.setCountry(request.getCountry());
        party.setIsActive(true); // always active on create

        Party saved = repository.save(party);
        return mapToResponse(saved);
    }

    @Override
    public PartyResponse update(Long businessId, Long id, PartyUpdateRequest request) {
        Party existing = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        existing.setName(request.getName());
        existing.setType(request.getType());
        existing.setGstin(request.getGstin());
        existing.setSinceDate(request.getSinceDate());
        existing.setCreditLimit(request.getCreditLimit());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setAddressLine1(request.getAddressLine1());
        existing.setCity(request.getCity());
        existing.setState(request.getState());
        existing.setPincode(request.getPincode());
        existing.setCountry(request.getCountry());

        Party saved = repository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    public List<PartyResponse> getAll(Long businessId) {
        return repository.findByBusinessIdAndIsActiveTrue(businessId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PartyResponse getById(Long businessId, Long id) {
        Party party = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        return mapToResponse(party);
    }

    @Override
    public void delete(Long businessId, Long id) {
        Party existing = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        existing.setIsActive(false); // 🔹 soft delete
        repository.save(existing);
    }

    // -------------------- Helper --------------------
    private PartyResponse mapToResponse(Party party) {
        PartyResponse response = new PartyResponse();
        response.setId(party.getId());
        response.setBusinessId(party.getBusinessId());
        response.setName(party.getName());
        response.setType(party.getType());
        response.setGstin(party.getGstin());
        response.setSinceDate(party.getSinceDate());
        response.setCreditLimit(party.getCreditLimit());
        response.setPhone(party.getPhone());
        response.setEmail(party.getEmail());
        response.setAddressLine1(party.getAddressLine1());
        response.setCity(party.getCity());
        response.setState(party.getState());
        response.setPincode(party.getPincode());
        response.setCountry(party.getCountry());
        return response;
    }
}