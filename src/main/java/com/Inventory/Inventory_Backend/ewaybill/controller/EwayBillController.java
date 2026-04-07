package com.Inventory.Inventory_Backend.ewaybill.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.ewaybill.dto.*;
import com.Inventory.Inventory_Backend.ewaybill.service.EWayBillService;
import jakarta.validation.Valid;
import org.flywaydb.core.internal.util.JsonUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eway-bills")
public class EwayBillController {

    private final EWayBillService service;

    private final BusinessContext businessContext;


    public EwayBillController(EWayBillService service, BusinessContext businessContext) {
        this.service = service;
        this.businessContext = businessContext;
    }

    // Create EWay Bill
    @PostMapping
    public ResponseEntity<EWayBillResponse> createEWayBill(
            @Valid @RequestBody EWayBillCreateRequest request) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        EWayBillResponse response = service.createEWayBill(businessId, request);
        return ResponseEntity.ok(response);
    }

    // Get Single Bill
    @GetMapping("/{id}")
    public ResponseEntity<EWayBillResponse> getEWayBill(
            @PathVariable Long id) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        EWayBillResponse response = service.getEWayBill(businessId, id);
        return ResponseEntity.ok(response);
    }

    // Update Bill
    @PutMapping("/{id}")
    public ResponseEntity<EWayBillResponse> updateEWayBill(
            @PathVariable Long id,
            @RequestBody EWayBillUpdateRequest request) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        EWayBillResponse response = service.updateEWayBill(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    // Delete Bill
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEWayBill(
            @PathVariable Long id) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        service.deleteEWayBill(businessId, id);
        return ResponseEntity.noContent().build();
    }

    // Update Vehicle
    @PatchMapping("/{id}/vehicle")
    public ResponseEntity<EWayBillResponse> updateVehicle(
            @PathVariable Long id,
            @RequestBody EWayBillVehicleUpdateRequest request) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        EWayBillResponse response = service.updateVehicle(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    // Extend Validity
    @PatchMapping("/{id}/extend-validity")
    public ResponseEntity<EWayBillResponse> extendValidity(
            @PathVariable Long id,
            @RequestBody EWayBillExtendValidityRequest request) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }
        EWayBillResponse response = service.extendValidity(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    // Cancel Bill
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEWayBill(
            @PathVariable Long id) {
        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }

        service.cancelEWayBill(businessId, id);
        return ResponseEntity.noContent().build();
    }

    //get all bills with pagination
    @GetMapping
    public Page<EWayBillResponse> getAllBills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long businessId = businessContext.getBusinessId();

        if (businessId == null) {
            throw new RuntimeException("Business ID missing");
        }

        return service.getAllBills(businessId, page, size);
    }

}
