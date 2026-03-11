package com.Inventory.Inventory_Backend.quotation.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationRequestDTO;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationResponseDTO;
import com.Inventory.Inventory_Backend.quotation.service.QuotationService;

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

        Long businessId = businessContext.getCurrentBusinessId();

        QuotationResponseDTO response =
                quotationService.createQuotation(businessId, request);

        return ResponseEntity.status(201).body(response);
    }

    // ============================================================
    // GET ALL QUOTATIONS
    // ============================================================

    @GetMapping
    public ResponseEntity<List<QuotationResponseDTO>> getAll() {

        Long businessId = businessContext.getCurrentBusinessId();

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

        Long businessId = businessContext.getCurrentBusinessId();

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

        Long businessId = businessContext.getCurrentBusinessId();

        quotationService.deleteQuotation(businessId, quotationId);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // CONVERT QUOTATION → SALES INVOICE
    // ============================================================

    @PostMapping("/{quotationId}/convert")
    public ResponseEntity<Long> convertToInvoice(
            @PathVariable Long quotationId) {

        Long businessId = businessContext.getCurrentBusinessId();

        Long invoiceId =
                quotationService.convertToSalesInvoice(businessId, quotationId);

        return ResponseEntity.ok(invoiceId);
    }
}