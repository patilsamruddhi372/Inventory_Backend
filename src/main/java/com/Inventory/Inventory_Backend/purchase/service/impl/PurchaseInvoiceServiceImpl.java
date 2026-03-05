package com.Inventory.Inventory_Backend.purchase.service.impl;

import com.Inventory.Inventory_Backend.common.BusinessContext;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceItemDTO;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.purchase.dto.PurchaseInvoiceResponseDTO;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoice;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoiceItem;
import com.Inventory.Inventory_Backend.purchase.exception.DuplicateBillNumberException;
import com.Inventory.Inventory_Backend.purchase.exception.PurchaseInvoiceNotFoundException;
import com.Inventory.Inventory_Backend.purchase.mapper.PurchaseInvoiceMapper;
import com.Inventory.Inventory_Backend.purchase.repository.PurchaseInvoiceRepository;
import com.Inventory.Inventory_Backend.purchase.service.PurchaseInvoiceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    private final PurchaseInvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;
    private final ItemRepository itemRepository;
    private final PurchaseInvoiceMapper mapper;
    private final BusinessContext businessContext;

    @PersistenceContext
    private EntityManager entityManager;

    // =========================================================
    // CREATE PURCHASE
    // =========================================================

    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO createPurchaseInvoice(PurchaseInvoiceRequestDTO request) {

        Long businessId = businessContext.getCurrentBusinessId();

        validatePartyBelongsToBusiness(request.getPartyId(), businessId);
        validateBillNumberIsUnique(businessId, request.getBillNumber());
        validateAllItemsBelongToBusiness(request.getItems(), businessId);
        validateNoDuplicateItemIdsInRequest(request.getItems());

        PurchaseInvoice invoice = mapper.toEntity(request);
        invoice.setBusinessId(businessId);

        // items are built in calculateInvoiceTotals
        invoice.setItems(new ArrayList<>());

        calculateInvoiceTotals(invoice, request);

        PurchaseInvoice saved = invoiceRepository.save(invoice);

        log.info("Created purchase invoice id={} for business={}", saved.getId(), businessId);
        return mapper.toResponseDTO(saved);
    }

    // =========================================================
    // GET PURCHASE BY ID
    // =========================================================

    @Override
    public PurchaseInvoiceResponseDTO getPurchaseInvoiceById(Long id, Long businessId) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdWithItemsAndParty(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        return mapper.toResponseDTO(invoice);
    }

    // =========================================================
    // GET ALL PURCHASES
    // =========================================================

    @Override
    public List<PurchaseInvoiceResponseDTO> getAllPurchaseInvoices(Long businessId) {

        List<PurchaseInvoice> invoices =
                invoiceRepository.findByBusinessIdOrderByCreatedAtDesc(businessId);

        return mapper.toResponseDTOList(invoices);
    }

    // =========================================================
    // UPDATE PURCHASE  ✅ FIXED
    // =========================================================

    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO updatePurchaseInvoice(Long id, PurchaseInvoiceRequestDTO request) {

        Long businessId = businessContext.getCurrentBusinessId();

        PurchaseInvoice invoice = invoiceRepository
                .findByIdWithItemsAndParty(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        validatePartyBelongsToBusiness(request.getPartyId(), businessId);
        validateBillNumberIsUniqueForUpdate(businessId, request.getBillNumber(), id);
        validateAllItemsBelongToBusiness(request.getItems(), businessId);
        validateNoDuplicateItemIdsInRequest(request.getItems());

        // update invoice header fields
        invoice.setPartyId(request.getPartyId());
        invoice.setBillNumber(request.getBillNumber().trim());
        invoice.setBillDate(request.getBillDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setNotes(request.getNotes());

        // 1) remove old items (orphanRemoval = true will delete them)
        invoice.getItems().clear();

        // 2) IMPORTANT: force DELETEs to hit DB before new INSERTs
        entityManager.flush();

        // 3) add new items + recalculate totals
        calculateInvoiceTotals(invoice, request);

        PurchaseInvoice saved = invoiceRepository.save(invoice);

        log.info("Updated purchase invoice id={} for business={}", saved.getId(), businessId);
        return mapper.toResponseDTO(saved);
    }

    // =========================================================
    // DELETE PURCHASE (Soft Delete)
    // =========================================================

    @Override
    @Transactional
    public void deletePurchaseInvoice(Long id) {

        Long businessId = businessContext.getCurrentBusinessId();

        PurchaseInvoice invoice = invoiceRepository
                .findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);

        log.info("Soft-deleted purchase invoice id={} for business={}", id, businessId);
    }

    // =========================================================
    // CALCULATE TOTALS
    // =========================================================

    private void calculateInvoiceTotals(PurchaseInvoice invoice, PurchaseInvoiceRequestDTO request) {

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (PurchaseInvoiceItemDTO itemDTO : request.getItems()) {

            BigDecimal base = itemDTO.getQuantity()
                    .multiply(itemDTO.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal gst = itemDTO.getGstRate() == null ? BigDecimal.ZERO : itemDTO.getGstRate();

            BigDecimal tax = base
                    .multiply(gst)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(base);
            totalTax = totalTax.add(tax);

            PurchaseInvoiceItem item = mapper.toItemEntity(itemDTO);
            item.setTotal(base.add(tax));

            invoice.addItem(item); // sets purchaseInvoice + businessId
        }

        BigDecimal grandTotal = subtotal.add(totalTax);

        BigDecimal amountPaid = request.getAmountPaid() == null
                ? BigDecimal.ZERO
                : request.getAmountPaid();

        BigDecimal balance = grandTotal.subtract(amountPaid);

        invoice.setSubtotal(subtotal);
        invoice.setTotalTax(totalTax);
        invoice.setGrandTotal(grandTotal);
        invoice.setAmountPaid(amountPaid);
        invoice.setBalance(balance);
        invoice.setStatus(deriveStatus(grandTotal, amountPaid));
    }

    // =========================================================
    // VALIDATIONS
    // =========================================================

    private void validateNoDuplicateItemIdsInRequest(List<PurchaseInvoiceItemDTO> items) {
        Set<Long> seen = new HashSet<>();
        for (PurchaseInvoiceItemDTO dto : items) {
            if (!seen.add(dto.getItemId())) {
                throw new IllegalArgumentException("Duplicate itemId in request: " + dto.getItemId());
            }
        }
    }

    private void validatePartyBelongsToBusiness(Long partyId, Long businessId) {

        if (!partyRepository.existsByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)) {
            throw new IllegalArgumentException("Active party not found with ID: " + partyId);
        }
    }

    private void validateBillNumberIsUnique(Long businessId, String billNumber) {

        if (invoiceRepository.existsByBusinessIdAndBillNumber(businessId, billNumber.trim())) {
            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateBillNumberIsUniqueForUpdate(Long businessId, String billNumber, Long invoiceId) {

        if (invoiceRepository.existsByBusinessIdAndBillNumberAndIdNot(
                businessId, billNumber.trim(), invoiceId)) {
            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateAllItemsBelongToBusiness(List<PurchaseInvoiceItemDTO> items, Long businessId) {

        for (PurchaseInvoiceItemDTO dto : items) {
            if (!itemRepository.existsByIdAndBusinessIdAndIsActiveTrue(dto.getItemId(), businessId)) {
                throw new IllegalArgumentException("Active item not found with ID: " + dto.getItemId());
            }
        }
    }

    private String deriveStatus(BigDecimal total, BigDecimal paid) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) return "pending";
        if (paid.compareTo(total) >= 0) return "paid";
        return "partial";
    }
}