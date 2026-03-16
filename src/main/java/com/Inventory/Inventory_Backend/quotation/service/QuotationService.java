package com.Inventory.Inventory_Backend.quotation.service;

import com.Inventory.Inventory_Backend.quotation.dto.QuotationRequestDTO;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationResponseDTO;

import java.util.List;

public interface QuotationService {

    // ============================================================
    // CREATE QUOTATION
    // ============================================================

    QuotationResponseDTO createQuotation(Long businessId,
                                         QuotationRequestDTO request);


    // ============================================================
    // GET ALL QUOTATIONS
    // ============================================================

    List<QuotationResponseDTO> getAllQuotations(Long businessId);


    // ============================================================
    // GET QUOTATION BY ID
    // ============================================================

    QuotationResponseDTO getQuotationById(Long businessId,
                                          Long quotationId);


    // ============================================================
    // SOFT DELETE QUOTATION
    // ============================================================

    void deleteQuotation(Long businessId,
                         Long quotationId);

    // ============================================================
    // UPDATE QUOTATION STATUS
    // ============================================================

    QuotationResponseDTO updateQuotationStatus(Long businessId,
                                               Long quotationId,
                                               String status);


    // ============================================================
    // CONVERT QUOTATION → SALES INVOICE
    // ============================================================

    Long convertToSalesInvoice(Long businessId,
                               Long quotationId);
}