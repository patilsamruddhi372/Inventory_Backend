package com.Inventory.Inventory_Backend.purchase.service;

import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceResponseDTO; // ✅ ADDED
import java.util.List;

public interface PurchaseInvoiceService {

    // ==========================================================
    // CREATE PURCHASE INVOICE
    // ==========================================================
    PurchaseInvoiceResponseDTO createPurchaseInvoice(
            Long businessId,
            PurchaseInvoiceRequestDTO requestDTO
    );

    // ==========================================================
    // GET PURCHASE INVOICE BY ID
    // ==========================================================
    PurchaseInvoiceResponseDTO getPurchaseInvoiceById(
            Long businessId,
            Long id
    );

    // ==========================================================
    // GET ALL PURCHASE INVOICES
    // ==========================================================
    List<PurchaseInvoiceResponseDTO> getAllPurchaseInvoices(
            Long businessId
    );

    // ==========================================================
    // UPDATE PURCHASE INVOICE
    // ==========================================================
    PurchaseInvoiceResponseDTO updatePurchaseInvoice(
            Long businessId,
            Long id,
            PurchaseInvoiceRequestDTO requestDTO
    );

    // ==========================================================
    // DELETE PURCHASE INVOICE (SOFT DELETE)
    // ==========================================================
    void deletePurchaseInvoice(
            Long businessId,
            Long id
    );

    // ==========================================================
    // CANCEL PURCHASE INVOICE (NEW)
    // ==========================================================
    PurchaseInvoiceResponseDTO cancelInvoice(
            Long businessId,
            Long id
    );
}