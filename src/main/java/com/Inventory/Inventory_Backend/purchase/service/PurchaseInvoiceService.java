package com.Inventory.Inventory_Backend.purchase.service;

import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceResponseDTO;

import java.util.List;

public interface PurchaseInvoiceService {

    // Create purchase invoice
    PurchaseInvoiceResponseDTO createPurchaseInvoice(
            PurchaseInvoiceRequestDTO requestDTO
    );

    // Get purchase by ID
    PurchaseInvoiceResponseDTO getPurchaseInvoiceById(Long id, Long businessId);

    // Get all purchases for a business
    List<PurchaseInvoiceResponseDTO> getAllPurchaseInvoices(Long businessId);

    // Update purchase invoice
    PurchaseInvoiceResponseDTO updatePurchaseInvoice(
            Long id,
            PurchaseInvoiceRequestDTO requestDTO
    );

    // Delete purchase invoice
    void deletePurchaseInvoice(Long id);
}