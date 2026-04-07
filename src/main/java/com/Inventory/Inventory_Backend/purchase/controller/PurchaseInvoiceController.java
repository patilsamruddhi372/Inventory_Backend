package com.Inventory.Inventory_Backend.purchase.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceResponseDTO;
import com.Inventory.Inventory_Backend.purchase.service.PurchaseInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService purchaseInvoiceService;
    private final BusinessContext businessContext;

    // ==========================================================
    // CREATE PURCHASE
    // ==========================================================
    @PostMapping
    public ResponseEntity<PurchaseInvoiceResponseDTO> createPurchaseInvoice(
            @Valid @RequestBody PurchaseInvoiceRequestDTO requestDTO) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("POST /api/purchases — businessId={}, billNumber={}",
                businessId, requestDTO.getBillNumber());

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.createPurchaseInvoice(businessId, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==========================================================
    // GET ALL PURCHASES
    // ==========================================================
    @GetMapping
    public ResponseEntity<List<PurchaseInvoiceResponseDTO>> getAllPurchaseInvoices() {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("GET /api/purchases — businessId={}", businessId);

        List<PurchaseInvoiceResponseDTO> purchases =
                purchaseInvoiceService.getAllPurchaseInvoices(businessId);

        return ResponseEntity.ok(purchases);
    }

    // ==========================================================
    // GET PURCHASE BY ID
    // ==========================================================
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponseDTO> getPurchaseInvoiceById(
            @PathVariable Long id) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("GET /api/purchases/{} — businessId={}", id, businessId);

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.getPurchaseInvoiceById(businessId, id);

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // UPDATE PURCHASE
    // ==========================================================
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponseDTO> updatePurchaseInvoice(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseInvoiceRequestDTO requestDTO) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("PUT /api/purchases/{} — businessId={}", id, businessId);

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.updatePurchaseInvoice(businessId, id, requestDTO);

        return ResponseEntity.ok(response);
    }

    // ==========================================================
    // DELETE PURCHASE (SOFT DELETE)
    // ==========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseInvoice(@PathVariable Long id) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("DELETE /api/purchases/{} — businessId={}", id, businessId);

        purchaseInvoiceService.deletePurchaseInvoice(businessId, id);

        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // CANCEL PURCHASE (NEW)
    // ==========================================================
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PurchaseInvoiceResponseDTO> cancelPurchaseInvoice(
            @PathVariable Long id) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        log.info("PATCH /api/purchases/{}/cancel — businessId={}", id, businessId);

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.cancelInvoice(businessId, id);

        return ResponseEntity.ok(response);
    }
}