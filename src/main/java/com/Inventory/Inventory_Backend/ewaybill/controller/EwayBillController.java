package com.Inventory.Inventory_Backend.ewaybill.controller;

import com.Inventory.Inventory_Backend.ewaybill.dto.*;
import com.Inventory.Inventory_Backend.ewaybill.service.EWayBillService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eway-bills")
public class EwayBillController {

    private final EWayBillService service;

    public EwayBillController(EWayBillService service) {
        this.service = service;
    }

    //Create EWay Bill
    @PostMapping
    public ResponseEntity<EWayBillResponse> createEWayBill(
            @RequestParam Long businessId,
            @Valid @RequestBody EWayBillCreateRequest request
    )
    {
        EWayBillResponse response = service.createEWayBill(businessId, request);
        return ResponseEntity.ok(response);
    }

    //Get Single Bill
    @GetMapping("/{id}")
    public ResponseEntity<EWayBillResponse> getEWayBill(
            @RequestParam Long businessId,
            @PathVariable Long id
    )
    {
        EWayBillResponse response = service.getEWayBill(businessId, id);
        return ResponseEntity.ok(response);
    }

    //Update Bill
    @PutMapping("/{id}")
    public ResponseEntity<EWayBillResponse> updateEWayBill(
            @RequestParam Long businessId,
            @PathVariable Long id,
            @RequestBody EWayBillUpdateRequest request
    )
    {
        EWayBillResponse response = service.updateEWayBill(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    //Delete Bill
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEWayBill(
            @RequestParam Long businessId,
            @PathVariable Long id
    )
    {
        service.deleteEWayBill(businessId, id);
        return ResponseEntity.noContent().build();
    }

    //Update Vehicle
    @PatchMapping("/{id}/vehicle")
    public ResponseEntity<EWayBillResponse> updateVehicle(
            @RequestParam Long businessId,
            @PathVariable Long id,
            @RequestBody EWayBillVehicleUpdateRequest request
    )
    {
        EWayBillResponse response = service.updateVehicle(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    //Extend Validity
    @PatchMapping("/{id}/extend-validity")
    public ResponseEntity<EWayBillResponse> extendValidity(
            @RequestParam Long businessId,
            @PathVariable Long id,
            @RequestBody EWayBillExtendValidityRequest request
    )
    {
        EWayBillResponse response = service.extendValidity(businessId, id, request);
        return ResponseEntity.ok(response);
    }

    //Cancle Bill
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEWayBill(
            @RequestParam Long businessId,
            @PathVariable Long id
    )
    {
        service.cancelEWayBill(businessId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<EWayBillResponse> getAllBills(
            @RequestParam Long businessId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAllBills(businessId, page, size);
    }

}
