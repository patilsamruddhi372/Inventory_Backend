package com.Inventory.Inventory_Backend.party.service;

import com.Inventory.Inventory_Backend.party.dto.PartyCreateRequest;
import com.Inventory.Inventory_Backend.party.dto.PartyResponse;
import com.Inventory.Inventory_Backend.party.dto.PartyUpdateRequest;
import com.Inventory.Inventory_Backend.party.entity.Party;
import com.Inventory.Inventory_Backend.party.entity.PartyType;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;

import com.Inventory.Inventory_Backend.purchase.repository.PurchaseInvoiceItemRepository;
import com.Inventory.Inventory_Backend.purchase.repository.PurchaseInvoiceRepository;
import com.Inventory.Inventory_Backend.sales.repository.SalesInvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyServiceImpl implements PartyService {

    private final PartyRepository repository;
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;
    private final SalesInvoiceRepository salesInvoiceRepository;
    private final PurchaseInvoiceItemRepository purchaseInvoiceItemRepository;

    public PartyServiceImpl(PartyRepository repository,
                            PurchaseInvoiceRepository purchaseInvoiceRepository, SalesInvoiceRepository salesInvoiceRepository,
                            PurchaseInvoiceItemRepository purchaseInvoiceItemRepository) {
        this.repository = repository;
        this.purchaseInvoiceRepository = purchaseInvoiceRepository;
        this.salesInvoiceRepository = salesInvoiceRepository;
        this.purchaseInvoiceItemRepository = purchaseInvoiceItemRepository;
    }

    // -------- CREATE --------
    @Override
    public PartyResponse create(Long businessId, PartyCreateRequest request) {

        if (request.getGstin() != null &&
                repository.existsByBusinessIdAndGstinAndIsActiveTrue(businessId, request.getGstin())) {
            throw new RuntimeException("GSTIN already exists");
        }

        if (request.getPhone() != null &&
                repository.existsByBusinessIdAndPhoneAndIsActiveTrue(businessId, request.getPhone())) {
            throw new RuntimeException("Mobile number already exists");
        }

        if (request.getEmail() != null &&
                repository.existsByBusinessIdAndEmailAndIsActiveTrue(businessId, request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Party party = new Party();
        party.setBusinessId(businessId);
        party.setName(request.getName().trim());
        party.setType(request.getType());
        party.setGstin(request.getGstin());
        party.setSinceDate(request.getSinceDate());
        party.setCreditLimit(request.getCreditLimit());
        party.setOpeningBalance(request.getOpeningBalance());
        party.setPhone(request.getPhone());
        party.setEmail(request.getEmail());
        party.setAddressLine1(request.getAddressLine1());
        party.setCity(request.getCity());
        party.setState(request.getState());
        party.setPincode(request.getPincode());
        party.setCountry(request.getCountry());
        party.setIsActive(true);

        Party saved = repository.save(party);
        return mapToResponse(saved);
    }

    // -------- FULL UPDATE (PUT) --------
    @Override
    public PartyResponse update(Long businessId, Long id, PartyUpdateRequest request) {

        Party existing = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        if (request.getGstin() != null &&
                repository.existsByBusinessIdAndGstinAndIdNotAndIsActiveTrue(businessId, request.getGstin(), id)) {
            throw new RuntimeException("GSTIN already exists");
        }

        if (request.getPhone() != null &&
                repository.existsByBusinessIdAndPhoneAndIdNotAndIsActiveTrue(businessId, request.getPhone(), id)) {
            throw new RuntimeException("Mobile number already exists");
        }

        if (request.getEmail() != null &&
                repository.existsByBusinessIdAndEmailAndIdNotAndIsActiveTrue(businessId, request.getEmail(), id)) {
            throw new RuntimeException("Email already exists");
        }

        existing.setName(request.getName());
        existing.setType(request.getType());
        existing.setGstin(request.getGstin());
        existing.setSinceDate(request.getSinceDate());
        existing.setCreditLimit(request.getCreditLimit());
        existing.setOpeningBalance(request.getOpeningBalance());
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

    // -------- PARTIAL UPDATE (PATCH) --------
    @Override
    public PartyResponse patchUpdate(Long businessId, Long id, PartyUpdateRequest request) {

        Party existing = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        if (request.getGstin() != null &&
                repository.existsByBusinessIdAndGstinAndIdNotAndIsActiveTrue(businessId, request.getGstin(), id)) {
            throw new RuntimeException("GSTIN already exists");
        }

        if (request.getPhone() != null &&
                repository.existsByBusinessIdAndPhoneAndIdNotAndIsActiveTrue(businessId, request.getPhone(), id)) {
            throw new RuntimeException("Mobile number already exists");
        }

        if (request.getEmail() != null &&
                repository.existsByBusinessIdAndEmailAndIdNotAndIsActiveTrue(businessId, request.getEmail(), id)) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getName() != null)
            existing.setName(request.getName());

        if (request.getType() != null)
            existing.setType(request.getType());

        if (request.getGstin() != null)
            existing.setGstin(request.getGstin());

        if (request.getSinceDate() != null)
            existing.setSinceDate(request.getSinceDate());

        if (request.getCreditLimit() != null)
            existing.setCreditLimit(request.getCreditLimit());

        if (request.getOpeningBalance() != null)
            existing.setOpeningBalance(request.getOpeningBalance());

        if (request.getPhone() != null)
            existing.setPhone(request.getPhone());

        if (request.getEmail() != null)
            existing.setEmail(request.getEmail());

        if (request.getAddressLine1() != null)
            existing.setAddressLine1(request.getAddressLine1());

        if (request.getCity() != null)
            existing.setCity(request.getCity());

        if (request.getState() != null)
            existing.setState(request.getState());

        if (request.getPincode() != null)
            existing.setPincode(request.getPincode());

        if (request.getCountry() != null)
            existing.setCountry(request.getCountry());

        Party saved = repository.save(existing);
        return mapToResponse(saved);
    }

    // -------- GET ALL --------
    @Override
    public List<PartyResponse> getAll(Long businessId) {
        return repository.findByBusinessIdAndIsActiveTrue(businessId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------- GET BY TYPE (🔥 NEW) --------
    @Override
    public List<PartyResponse> getByType(Long businessId, PartyType type) {

        if (type == PartyType.CUSTOMER) {
            return repository
                    .findByBusinessIdAndTypeInAndIsActiveTrue(
                            businessId,
                            List.of(PartyType.CUSTOMER, PartyType.BOTH))
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        if (type == PartyType.SUPPLIER) {
            return repository
                    .findByBusinessIdAndTypeInAndIsActiveTrue(
                            businessId,
                            List.of(PartyType.SUPPLIER, PartyType.BOTH))
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return getAll(businessId);
    }

    // -------- GET BY MULTIPLE TYPES (PRO 🔥) --------
    @Override
    public List<PartyResponse> getByTypes(Long businessId, List<PartyType> types) {

        return repository
                .findByBusinessIdAndTypeInAndIsActiveTrue(businessId, types)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // -------- GET BY ID --------
    @Override
    public PartyResponse getById(Long businessId, Long id) {

        Party party = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        return mapToResponse(party);
    }

    // -------- DELETE (SOFT DELETE) --------
    @Override
    public void delete(Long businessId, Long id) {

        Party existing = repository.findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        //check dependencies
        boolean usedInSales =
                salesInvoiceRepository.existsByPartyIdAndBusinessIdAndIsDeletedFalse(id, businessId);

        boolean usedInPurchase =
                purchaseInvoiceRepository.existsByPartyIdAndBusinessIdAndIsDeletedFalse(id, businessId);

        if(usedInSales || usedInPurchase){
            throw new RuntimeException("Cannot delete party used in invoices");
        }

        //soft delete
        existing.setIsActive(false);
        repository.save(existing);
    }

    // -------- HELPER --------
    private PartyResponse mapToResponse(Party party) {

        PartyResponse response = new PartyResponse();

        response.setId(party.getId());
        response.setBusinessId(party.getBusinessId());
        response.setName(party.getName());
        response.setType(party.getType());
        response.setGstin(party.getGstin());
        response.setSinceDate(party.getSinceDate());
        response.setCreditLimit(party.getCreditLimit());
        response.setOpeningBalance(party.getOpeningBalance());
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