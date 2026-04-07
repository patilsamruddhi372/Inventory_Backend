package com.Inventory.Inventory_Backend.sales.controller;

import com.Inventory.Inventory_Backend.common.BusinessContext; // ✅ ADDED
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceResponseDTO;
import com.Inventory.Inventory_Backend.sales.service.SalesInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalesInvoiceController {

    private final SalesInvoiceService salesInvoiceService;
    private final BusinessContext businessContext; // ✅ ADDED

    // =========================
    // CREATE SALES INVOICE
    // =========================
    @PostMapping
    public ResponseEntity<SalesInvoiceResponseDTO> createSalesInvoice(
            @Valid @RequestBody SalesInvoiceRequestDTO request) {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        SalesInvoiceResponseDTO response =
                salesInvoiceService.createSalesInvoice(businessId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // GET ALL SALES INVOICES
    // =========================
    @GetMapping
    public ResponseEntity<List<SalesInvoiceResponseDTO>> getAllSalesInvoices() {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        List<SalesInvoiceResponseDTO> invoices =
                salesInvoiceService.getAllSalesInvoices(businessId);

        return ResponseEntity.ok(invoices);
    }

    // =========================
    // GET ONE SALES INVOICE
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<SalesInvoiceResponseDTO> getSalesInvoiceById(
            @PathVariable("id") Long invoiceId) {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        SalesInvoiceResponseDTO response =
                salesInvoiceService.getSalesInvoiceById(businessId, invoiceId);

        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE SALES INVOICE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<SalesInvoiceResponseDTO> updateSalesInvoice(
            @PathVariable("id") Long invoiceId,
            @Valid @RequestBody SalesInvoiceRequestDTO request) {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        SalesInvoiceResponseDTO response =
                salesInvoiceService.updateSalesInvoice(businessId, invoiceId, request);

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE SALES INVOICE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesInvoice(
            @PathVariable("id") Long invoiceId) {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        salesInvoiceService.deleteSalesInvoice(businessId, invoiceId);

        return ResponseEntity.noContent().build();
    }

    // =========================
    // CANCEL SALES INVOICE (NEW)
    // =========================
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SalesInvoiceResponseDTO> cancelSalesInvoice(
            @PathVariable("id") Long invoiceId) {

        Long businessId = businessContext.getBusinessId(); // ✅ UPDATED

        if(businessId == null) {
            throw new RuntimeException("Business Id is missing in request");
        }

        SalesInvoiceResponseDTO response =
                salesInvoiceService.cancelInvoice(businessId, invoiceId);

        return ResponseEntity.ok(response);
    }
}