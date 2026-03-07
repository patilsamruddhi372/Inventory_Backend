package com.Inventory.Inventory_Backend.sales.controller;

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

    // =========================
    // CREATE SALES INVOICE
    // =========================
    @PostMapping
    public ResponseEntity<SalesInvoiceResponseDTO> createSalesInvoice(
            @RequestHeader("X-Business-Id") Long businessId,
            @Valid @RequestBody SalesInvoiceRequestDTO request) {

        SalesInvoiceResponseDTO response =
                salesInvoiceService.createSalesInvoice(businessId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // GET ALL SALES INVOICES
    // =========================
    @GetMapping
    public ResponseEntity<List<SalesInvoiceResponseDTO>> getAllSalesInvoices(
            @RequestHeader("X-Business-Id") Long businessId) {

        List<SalesInvoiceResponseDTO> invoices =
                salesInvoiceService.getAllSalesInvoices(businessId);

        return ResponseEntity.ok(invoices);
    }

    // =========================
    // GET ONE SALES INVOICE
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<SalesInvoiceResponseDTO> getSalesInvoiceById(
            @RequestHeader("X-Business-Id") Long businessId,
            @PathVariable("id") Long invoiceId) {

        SalesInvoiceResponseDTO response =
                salesInvoiceService.getSalesInvoiceById(businessId, invoiceId);

        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE SALES INVOICE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<SalesInvoiceResponseDTO> updateSalesInvoice(
            @RequestHeader("X-Business-Id") Long businessId,
            @PathVariable("id") Long invoiceId,
            @Valid @RequestBody SalesInvoiceRequestDTO request) {

        SalesInvoiceResponseDTO response =
                salesInvoiceService.updateSalesInvoice(businessId, invoiceId, request);

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE SALES INVOICE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesInvoice(
            @RequestHeader("X-Business-Id") Long businessId,
            @PathVariable("id") Long invoiceId) {

        salesInvoiceService.deleteSalesInvoice(businessId, invoiceId);

        return ResponseEntity.noContent().build();
    }
}