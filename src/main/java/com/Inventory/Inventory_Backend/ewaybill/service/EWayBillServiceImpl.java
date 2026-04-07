package com.Inventory.Inventory_Backend.ewaybill.service;

import com.Inventory.Inventory_Backend.ewaybill.dto.*;
import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBill;
import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBillStatus;
import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBillVehicleAudit;
import com.Inventory.Inventory_Backend.party.entity.Party;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import com.Inventory.Inventory_Backend.ewaybill.repository.EWayBillRepository;
import com.Inventory.Inventory_Backend.ewaybill.repository.EWayBillVehicleAuditRepository;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import com.Inventory.Inventory_Backend.sales.repository.SalesInvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EWayBillServiceImpl implements EWayBillService {

    private final EWayBillRepository repository;

    public EWayBillServiceImpl(EWayBillRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private EWayBillVehicleAuditRepository auditRepository;

    @Autowired
    private SalesInvoiceRepository salesInvoiceRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Override
    public EWayBillResponse createEWayBill(Long businessId, EWayBillCreateRequest request) {

        SalesInvoice invoice = salesInvoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(
                        request.getSalesInvoiceId(),
                        businessId)
                .orElseThrow(() -> new RuntimeException("Sales Invoice not found"));

        if(!invoice.getBusinessId().equals(businessId)){
            throw new RuntimeException("Cross-business invoice access detected");
        }

        Party buyer = partyRepository
                .findByIdAndBusinessIdAndIsActiveTrue(
                        invoice.getPartyId(),
                        businessId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Optional<EWayBill> existing =
                repository.findBySalesInvoiceIdAndBusinessId(
                        request.getSalesInvoiceId(),
                        businessId);

        if (existing.isPresent()) {
            throw new RuntimeException("EWay Bill already exists for this invoice");
        }

        if (invoice.getGrandTotal().compareTo(new BigDecimal("50000")) < 0) {
            throw new IllegalArgumentException("EWay Bill not required for invoices below ₹50,000");
        }

        EWayBill entity = new EWayBill();

        entity.setSalesInvoice(invoice);
        entity.setSalesInvoiceId(invoice.getId());
        entity.setBusinessId(businessId);
        // entity.setSalesInvoiceId(request.getSalesInvoiceId());

        entity.setInvoiceNumber(invoice.getInvoiceNumber());
        entity.setInvoiceDate(invoice.getInvoiceDate());
        entity.setTotalInvoiceValue(invoice.getGrandTotal());

        entity.setEwayBillNumber(generateEWayBillNumber(businessId));

        entity.setTransporterId(request.getTransporterId());
        entity.setTransporterName(request.getTransporterName());
        entity.setTransporterDocumentNo(request.getTransportDocumentNumber());
        entity.setTransporterDocumentDate(request.getTransportDocumentDate());

        entity.setTransportMode(request.getTransportMode());
        entity.setVehicleNumber(request.getVehicleNumber());

        entity.setDistanceKm(request.getDistanceKm());

        entity.setValidFrom(LocalDateTime.now());
        entity.setValidUntil(calculateValidity(request.getDistanceKm()));

        entity.setStatus(EWayBillStatus.ACTIVE);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // =============================
        // SELLER DETAILS (HARDCODED)
        // =============================
        entity.setSellerGstin("27ABCDE1234F1Z5");
        entity.setSellerBusinessName("My Business Pvt Ltd");
        entity.setSellerState("Maharashtra");

        // =============================
        // BUYER DETAILS (FROM PARTY)
        // =============================
        entity.setBuyerGstin(buyer.getGstin());
        entity.setBuyerBusinessName(buyer.getName());
        entity.setBuyerState(buyer.getState());

        repository.save(entity);

        return mapToResponse(entity);
    }

    @Override
    public EWayBillResponse getEWayBill(Long businessId, Long id) {
        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        return mapToResponse(entity);
    }

    @Override
    public List<EWayBillResponse> getAllEWayBills(Long businessId) {
        List<EWayBill> bills = repository.findByBusinessIdAndIsActiveTrue(businessId);

        return bills.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EWayBillResponse updateEWayBill(Long businessId, Long id, EWayBillUpdateRequest request) {
        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        validateBillEditable(entity);

        entity.setTransporterId(request.getTransporterId());
        entity.setTransporterName(request.getTransporterName());
        entity.setTransporterDocumentNo(request.getTransportDocumentNumber());
        entity.setTransporterDocumentDate(request.getTransportDocumentDate());

        entity.setTransportMode(request.getTransportMode());
        entity.setVehicleNumber(request.getVehicleNumber());

        entity.setDistanceKm(request.getDistanceKm());

        entity.setValidUntil(calculateValidity(request.getDistanceKm()));

        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

        return mapToResponse(entity);
    }

    @Override
    public void deleteEWayBill(Long businessId, Long id) {

        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        entity.setActive(false);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);

    }

    @Transactional
    @Override
    public EWayBillResponse updateVehicle(Long businessId, Long id, EWayBillVehicleUpdateRequest request) {
        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        validateBillEditable(entity);

        // Store old vehicle number BEFORE updating
        String oldVehicle = entity.getVehicleNumber();

        // Update vehicle details
        entity.setVehicleNumber(request.getVehicleNumber());
        entity.setTransportMode(request.getTransportMode());
        entity.setUpdatedAt(LocalDateTime.now());

        // Save audit record
        EWayBillVehicleAudit audit = new EWayBillVehicleAudit();
        audit.setEwayBillId(entity.getId());
        audit.setOldVehicleNumber(oldVehicle);
        audit.setNewVehicleNumber(request.getVehicleNumber());
        audit.setBusinessId(businessId);
        audit.setUpdatedAt(LocalDateTime.now());

        auditRepository.save(audit);

        return mapToResponse(entity);

    }

    @Override
    public EWayBillResponse extendValidity(Long businessId, Long id, EWayBillExtendValidityRequest request) {
        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        validateBillEditable(entity);

        // Use distance from request if provided, else use existing
        Integer distanceKm = (request.getDistanceKm() != null) ? request.getDistanceKm() : entity.getDistanceKm();

        if (distanceKm == null) {
            throw new RuntimeException("Distance must be provided for EWayBill generation");
        }

        // Optionally update entity distance
        if (request.getDistanceKm() != null) {
            entity.setDistanceKm(distanceKm);
        }

        // Use existing calculateValidity method
        entity.setValidUntil(calculateValidity(distanceKm)); // <- only 1 param

        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        return mapToResponse(entity);
    }

    @Override
    public void cancelEWayBill(Long businessId, Long id) {
        EWayBill entity = repository
                .findByIdAndBusinessIdAndIsActiveTrue(id, businessId)
                .orElseThrow(() -> new RuntimeException("EWayBill not found"));

        entity.setStatus(EWayBillStatus.CANCELLED);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);
    }

    @Override
    public Page<EWayBillResponse> getAllBills(Long businessId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<EWayBill> bills = repository.findByBusinessIdAndIsActiveTrue(businessId, pageable);

        return bills.map(this::mapToResponse);
    }

    private LocalDateTime calculateValidity(Integer distanceKm) {
        if (distanceKm == null || distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be provided for EWayBill generation");
        }
        int days = (int) Math.ceil(distanceKm / 100.0);
        return LocalDateTime.now().plusDays(days);
    }

    private String generateEWayBillNumber(Long businessId) {
        int year = LocalDateTime.now().getYear();

        Optional<EWayBill> lastBill = repository.findTopByBusinessIdOrderByIdDesc(businessId);

        int nextNumber = 1;

        if (lastBill.isPresent()) {
            String lastNumber = lastBill.get().getEwayBillNumber();
            if (lastNumber != null && lastNumber.contains("-")) {
                String[] parts = lastNumber.split("-");
                int lastSequence = Integer.parseInt(parts[2]);
                nextNumber = lastSequence + 1;
            }
        }

        return String.format("EWB-%d-%06d", year, nextNumber);
    }

    private EWayBillResponse mapToResponse(EWayBill entity) {
        EWayBillResponse response = new EWayBillResponse();

        response.setId(entity.getId());
        response.setEwayBillNumber(entity.getEwayBillNumber());
        response.setInvoiceNumber(entity.getInvoiceNumber());
        response.setInvoiceDate(entity.getInvoiceDate());
        response.setTotalInvoiceValue(entity.getTotalInvoiceValue());

        // Seller Details
        response.setSellerBusinessName(entity.getSellerBusinessName());
        response.setSellerGstin(entity.getSellerGstin()); // Added
        response.setSellerState(entity.getSellerState()); // Added

        // Buyer Details
        response.setBuyerName(entity.getBuyerBusinessName());
        response.setBuyerGstin(entity.getBuyerGstin());   // Added
        response.setBuyerState(entity.getBuyerState());   // Added

        // Transporter Details
        response.setTransporterId(entity.getTransporterId()); // Added
        response.setTransporterName(entity.getTransporterName()); // Added
        response.setTransporterDocumentNo(entity.getTransporterDocumentNo()); // Added
        response.setTransporterDocumentDate(entity.getTransporterDocumentDate()); // Added

        response.setVehicleNumber(entity.getVehicleNumber());
        response.setTransportMode(entity.getTransportMode());

        response.setDistanceKm(entity.getDistanceKm());

        response.setValidFrom(entity.getValidFrom());
        response.setValidUntil(entity.getValidUntil());

        response.setStatus(entity.getStatus());

        if (entity.getValidUntil() != null) {
            long daysRemaining = Duration.between(LocalDateTime.now(), entity.getValidUntil()).toDays();
            response.setDaysRemaining(daysRemaining);
        }

        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }

    private void validateBillEditable(EWayBill bill) {
        if (bill.getStatus() == EWayBillStatus.EXPIRED) {
            throw new RuntimeException("EWay Bill is expired and cannot be edited");
        }

        if (bill.getStatus() == EWayBillStatus.CANCELLED) {
            throw new RuntimeException("EWay Bill is cancelled and cannot be edited");
        }

    }
}
