package com.Inventory.Inventory_Backend.purchase.service.impl;

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
import com.Inventory.Inventory_Backend.stock.service.StockService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    private final PurchaseInvoiceRepository invoiceRepository;
    private final PartyRepository partyRepository;
    private final ItemRepository itemRepository;
    private final PurchaseInvoiceMapper mapper;
    private final StockService stockService;

    @PersistenceContext
    private EntityManager entityManager;

    // =========================================================
    // CREATE PURCHASE
    // =========================================================
    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO createPurchaseInvoice(
            Long businessId,
            PurchaseInvoiceRequestDTO request) {

        validatePartyBelongsToBusiness(request.getPartyId(), businessId);
        validateBillNumberIsUnique(businessId, request.getBillNumber());
        validateAllItemsBelongToBusiness(request.getItems(), businessId);
        validateNoDuplicateItemIdsInRequest(request.getItems());

        PurchaseInvoice invoice = mapper.toEntity(request);

        invoice.setBusinessId(businessId);
        invoice.setPaymentType(request.getPaymentType());   // ✅ ADDED
        invoice.setItems(new ArrayList<>());

        calculateInvoiceTotals(invoice, request);

        PurchaseInvoice saved = invoiceRepository.save(invoice);

        // UPDATE STOCK
        for (PurchaseInvoiceItem item : saved.getItems()) {

            stockService.increaseStock(
                    saved.getBusinessId(),
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        log.info("Created purchase invoice id={} business={}", saved.getId(), businessId);

        return mapper.toResponseDTO(saved);
    }

    // =========================================================
    // GET BY ID
    // =========================================================
    @Override
    public PurchaseInvoiceResponseDTO getPurchaseInvoiceById(Long businessId, Long id) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdWithItemsAndParty(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        return mapper.toResponseDTO(invoice);
    }

    // =========================================================
    // GET ALL
    // =========================================================
    @Override
    public List<PurchaseInvoiceResponseDTO> getAllPurchaseInvoices(Long businessId) {

        List<PurchaseInvoice> invoices =
                invoiceRepository.findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(businessId);

        return mapper.toResponseDTOList(invoices);
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO updatePurchaseInvoice(
            Long businessId,
            Long id,
            PurchaseInvoiceRequestDTO request) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdWithItemsAndParty(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        validatePartyBelongsToBusiness(request.getPartyId(), businessId);
        validateBillNumberIsUniqueForUpdate(businessId, request.getBillNumber(), id);
        validateAllItemsBelongToBusiness(request.getItems(), businessId);
        validateNoDuplicateItemIdsInRequest(request.getItems());

        invoice.setPartyId(request.getPartyId());
        invoice.setBillNumber(request.getBillNumber().trim());
        invoice.setBillDate(request.getBillDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setPaymentType(request.getPaymentType());   // ✅ ADDED
        invoice.setNotes(request.getNotes());

        invoice.getItems().clear();
        entityManager.flush();

        calculateInvoiceTotals(invoice, request);

        PurchaseInvoice saved = invoiceRepository.save(invoice);

        // UPDATE STOCK AGAIN
        for (PurchaseInvoiceItem item : saved.getItems()) {

            stockService.increaseStock(
                    saved.getBusinessId(),
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        log.info("Updated purchase invoice id={} business={}", saved.getId(), businessId);

        return mapper.toResponseDTO(saved);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Override
    @Transactional
    public void deletePurchaseInvoice(Long businessId, Long id) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        invoice.setIsDeleted(true);

        invoiceRepository.save(invoice);

        log.info("Deleted purchase invoice id={} business={}", id, businessId);
    }

    // =========================================================
    // CALCULATE TOTALS
    // =========================================================
    private void calculateInvoiceTotals(PurchaseInvoice invoice,
                                        PurchaseInvoiceRequestDTO request) {

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (PurchaseInvoiceItemDTO itemDTO : request.getItems()) {

            BigDecimal base = itemDTO.getQuantity()
                    .multiply(itemDTO.getRate())
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal gst = itemDTO.getGstRate() == null
                    ? BigDecimal.ZERO
                    : itemDTO.getGstRate();

            BigDecimal tax = base
                    .multiply(gst)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(base);
            totalTax = totalTax.add(tax);

            PurchaseInvoiceItem item = mapper.toItemEntity(itemDTO);
            item.setTotal(base.add(tax));

            invoice.addItem(item);
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

                throw new IllegalArgumentException(
                        "Duplicate itemId in request: " + dto.getItemId());
            }
        }
    }

    private void validatePartyBelongsToBusiness(Long partyId, Long businessId) {

        if (!partyRepository.existsByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)) {

            throw new IllegalArgumentException(
                    "Active party not found with ID: " + partyId);
        }
    }

    private void validateBillNumberIsUnique(Long businessId, String billNumber) {

        if (invoiceRepository.existsByBusinessIdAndBillNumberAndIsDeletedFalse(
                businessId, billNumber.trim())) {

            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateBillNumberIsUniqueForUpdate(Long businessId,
                                                     String billNumber,
                                                     Long invoiceId) {

        if (invoiceRepository.existsByBusinessIdAndBillNumberAndIdNotAndIsDeletedFalse(
                businessId, billNumber.trim(), invoiceId)) {

            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateAllItemsBelongToBusiness(List<PurchaseInvoiceItemDTO> items,
                                                  Long businessId) {

        for (PurchaseInvoiceItemDTO dto : items) {

            if (!itemRepository.existsByIdAndBusinessIdAndIsActiveTrue(
                    dto.getItemId(), businessId)) {

                throw new IllegalArgumentException(
                        "Active item not found with ID: " + dto.getItemId());
            }
        }
    }

    private String deriveStatus(BigDecimal total, BigDecimal paid) {

        if (paid.compareTo(BigDecimal.ZERO) == 0) return "pending";
        if (paid.compareTo(total) >= 0) return "paid";
        return "partial";
    }
}