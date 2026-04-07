package com.Inventory.Inventory_Backend.quotation.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationRequestDTO;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationResponseDTO;
import com.Inventory.Inventory_Backend.quotation.service.QuotationService;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationStatusUpdateRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;
    private final BusinessContext businessContext;

    // ============================================================
    // CREATE QUOTATION
    // ============================================================

    @PostMapping
    public ResponseEntity<QuotationResponseDTO> create(
            @Valid @RequestBody QuotationRequestDTO request) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        QuotationResponseDTO response =
                quotationService.createQuotation(businessId, request);

        return ResponseEntity.status(201).body(response);
    }

    // ============================================================
    // GET ALL QUOTATIONS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<QuotationResponseDTO>> getAll() {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        List<QuotationResponseDTO> quotations =
                quotationService.getAllQuotations(businessId);

        return ResponseEntity.ok(quotations);
    }

    // ============================================================
    // GET QUOTATION BY ID
    // ============================================================

    @GetMapping("/{quotationId}")
    public ResponseEntity<QuotationResponseDTO> getById(
            @PathVariable Long quotationId) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        QuotationResponseDTO quotation =
                quotationService.getQuotationById(businessId, quotationId);

        return ResponseEntity.ok(quotation);
    }

    // ============================================================
    // DELETE QUOTATION (SOFT DELETE)
    // ============================================================

    @DeleteMapping("/{quotationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long quotationId) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        quotationService.deleteQuotation(businessId, quotationId);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // UPDATE QUOTATION STATUS
    // ============================================================

    @PatchMapping("/{quotationId}/status")
    public ResponseEntity<QuotationResponseDTO> updateStatus(
            @PathVariable Long quotationId,
            @RequestBody QuotationStatusUpdateRequestDTO request) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        QuotationResponseDTO response =
                quotationService.updateQuotationStatus(
                        businessId,
                        quotationId,
                        request.getStatus()
                );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // CONVERT QUOTATION → SALES INVOICE
    // ============================================================

    @PostMapping("/{quotationId}/convert")
    public ResponseEntity<Long> convertToInvoice(
            @PathVariable Long quotationId) {

        Long businessId = businessContext.getBusinessId();

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        Long invoiceId =
                quotationService.convertToSalesInvoice(businessId, quotationId);

        return ResponseEntity.ok(invoiceId);
    }
}