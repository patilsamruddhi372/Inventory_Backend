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
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService purchaseInvoiceService;
    private final BusinessContext businessContext;

    /**
     * CREATE PURCHASE
     * POST /api/purchases
     */
    @PostMapping
    public ResponseEntity<PurchaseInvoiceResponseDTO> createPurchaseInvoice(
            @Valid @RequestBody PurchaseInvoiceRequestDTO requestDTO) {

        log.info("POST /api/purchases — bill: {}", requestDTO.getBillNumber());

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.createPurchaseInvoice(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET ALL PURCHASES
     * GET /api/purchases
     */
    @GetMapping
    public ResponseEntity<List<PurchaseInvoiceResponseDTO>> getAllPurchaseInvoices() {

        Long businessId = businessContext.getCurrentBusinessId();

        log.info("GET /api/purchases — business: {}", businessId);

        List<PurchaseInvoiceResponseDTO> list =
                purchaseInvoiceService.getAllPurchaseInvoices(businessId);

        return ResponseEntity.ok(list);
    }

    /**
     * GET PURCHASE BY ID
     * GET /api/purchases/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponseDTO> getPurchaseInvoiceById(
            @PathVariable Long id) {

        Long businessId = businessContext.getCurrentBusinessId();

        log.info("GET /api/purchases/{} — business: {}", id, businessId);

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.getPurchaseInvoiceById(id, businessId);

        return ResponseEntity.ok(response);
    }

    /**
     * UPDATE PURCHASE
     * PUT /api/purchases/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseInvoiceResponseDTO> updatePurchaseInvoice(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseInvoiceRequestDTO requestDTO) {

        log.info("PUT /api/purchases/{} — bill: {}", id, requestDTO.getBillNumber());

        PurchaseInvoiceResponseDTO response =
                purchaseInvoiceService.updatePurchaseInvoice(id, requestDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE PURCHASE
     * DELETE /api/purchases/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseInvoice(@PathVariable Long id) {

        log.info("DELETE /api/purchases/{}", id);

        purchaseInvoiceService.deletePurchaseInvoice(id);

        return ResponseEntity.noContent().build();
    }
}