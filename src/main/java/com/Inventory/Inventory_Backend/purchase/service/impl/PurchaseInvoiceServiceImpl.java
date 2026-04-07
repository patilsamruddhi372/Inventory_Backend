package com.Inventory.Inventory_Backend.purchase.service.impl;

import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import com.Inventory.Inventory_Backend.party.entity.PartyType;
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

    // ================= CREATE =================
    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO createPurchaseInvoice(
            Long businessId,
            PurchaseInvoiceRequestDTO request) {

        validatePartyBelongsToBusiness(request.getPartyId(), businessId);
        validateBillNumberIsUnique(businessId, request.getBillNumber());
        validateAllItemsBelongToBusiness(request.getItems(), businessId);

        PurchaseInvoice invoice = mapper.toEntity(request);

        invoice.setBusinessId(businessId);
        invoice.setItems(new ArrayList<>());

        calculateInvoiceTotals(invoice, request);

        invoiceRepository.save(invoice);
        System.out.println("Items count: " + invoice.getItems().size());
        for (PurchaseInvoiceItem item : invoice.getItems()) {
            stockService.increaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    invoice.getId()
            );
        }

        return mapper.toResponseDTO(invoice);
    }

    // ================= GET =================
    @Override
    public PurchaseInvoiceResponseDTO getPurchaseInvoiceById(Long businessId, Long id) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdWithItemsAndParty(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        return mapper.toResponseDTO(invoice);
    }

    @Override
    public List<PurchaseInvoiceResponseDTO> getAllPurchaseInvoices(Long businessId) {

        return mapper.toResponseDTOList(
                invoiceRepository.findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(businessId)
        );
    }

    // ================= UPDATE =================
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

        //step 1: restore old stock first
        for(PurchaseInvoiceItem item : invoice.getItems()){
            stockService.decreaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    invoice.getId()
            );
        }
        //thn clear items
        invoice.getItems().clear();

        //update fields
        invoice.setPartyId(request.getPartyId());
        invoice.setBillNumber(request.getBillNumber());

        calculateInvoiceTotals(invoice, request);

        PurchaseInvoice saved = invoiceRepository.save(invoice);

        //step 2: apply new stock
        for(PurchaseInvoiceItem item : saved.getItems()) {
            stockService.increaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        return mapper.toResponseDTO(saved);
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deletePurchaseInvoice(Long businessId, Long id) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        if (invoice.getAmountPaid() != null &&
                invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete purchase with payment");
        }

        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    }

    // ================= CANCEL =================
    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO cancelInvoice(Long businessId, Long id) {

        PurchaseInvoice invoice = invoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(id, businessId)
                .orElseThrow(() -> new PurchaseInvoiceNotFoundException(id, businessId));

        for(PurchaseInvoiceItem item : invoice.getItems()){
            stockService.decreaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    invoice.getId()
            );
        }

        invoice.setStatus("CANCELLED");

        return mapper.toResponseDTO(invoiceRepository.save(invoice));
    }

    // ================= VALIDATIONS =================

    private void validatePartyBelongsToBusiness(Long partyId, Long businessId) {

        var party = partyRepository
                .findByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Party not found"));

        // ✅ ENUM FIX
        if (party.getType() != PartyType.SUPPLIER &&
                party.getType() != PartyType.BOTH) {

            throw new IllegalArgumentException("Only SUPPLIER allowed");
        }
    }

    private void validateBillNumberIsUnique(Long businessId, String billNumber) {

        if (invoiceRepository.existsByBusinessIdAndBillNumberAndIsDeletedFalse(
                businessId, billNumber)) {

            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateBillNumberIsUniqueForUpdate(Long businessId,
                                                     String billNumber,
                                                     Long id) {

        if (invoiceRepository.existsByBusinessIdAndBillNumberAndIdNotAndIsDeletedFalse(
                businessId, billNumber, id)) {

            throw new DuplicateBillNumberException(billNumber);
        }
    }

    private void validateAllItemsBelongToBusiness(List<PurchaseInvoiceItemDTO> items,
                                                  Long businessId) {

        for (PurchaseInvoiceItemDTO dto : items) {
            if (!itemRepository.existsByIdAndBusinessIdAndIsActiveTrue(
                    dto.getItemId(), businessId)) {

                throw new IllegalArgumentException("Item not found: " + dto.getItemId());
            }
        }
    }

    // ================= CALCULATIONS =================

    private void calculateInvoiceTotals(PurchaseInvoice invoice,
                                        PurchaseInvoiceRequestDTO request) {

        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseInvoiceItemDTO item : request.getItems()) {
            BigDecimal amount = item.getQuantity().multiply(item.getRate());
            total = total.add(amount);

            PurchaseInvoiceItem entity = mapper.toItemEntity(item);
            //multi tenant safety
            entity.setBusinessId(invoice.getBusinessId());
            invoice.addItem(entity);
        }

        invoice.setGrandTotal(total);
        invoice.setAmountPaid(
                request.getAmountPaid() == null ? BigDecimal.ZERO : request.getAmountPaid()
        );
    }
}