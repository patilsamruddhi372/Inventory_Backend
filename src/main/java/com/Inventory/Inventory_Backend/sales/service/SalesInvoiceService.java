package com.Inventory.Inventory_Backend.sales.service;

import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceResponseDTO;

import java.util.List;

public interface SalesInvoiceService {

    // ==========================================================
    // CREATE SALES INVOICE
    // ==========================================================
    SalesInvoiceResponseDTO createSalesInvoice(
            Long businessId,
            SalesInvoiceRequestDTO request
    );

    // ==========================================================
    // GET ALL SALES INVOICES
    // ==========================================================
    List<SalesInvoiceResponseDTO> getAllSalesInvoices(
            Long businessId
    );

    // ==========================================================
    // GET SALES INVOICE BY ID
    // ==========================================================
    SalesInvoiceResponseDTO getSalesInvoiceById(
            Long businessId,
            Long invoiceId
    );

    // ==========================================================
    // UPDATE SALES INVOICE
    // ==========================================================
    SalesInvoiceResponseDTO updateSalesInvoice(
            Long businessId,
            Long invoiceId,
            SalesInvoiceRequestDTO request
    );

    // ==========================================================
    // DELETE SALES INVOICE (Soft Delete)
    // ==========================================================
    void deleteSalesInvoice(
            Long businessId,
            Long invoiceId
    );

    // ==========================================================
    // CANCEL SALES INVOICE
    // ==========================================================
    SalesInvoiceResponseDTO cancelInvoice(
            Long businessId,
            Long invoiceId
    );
}