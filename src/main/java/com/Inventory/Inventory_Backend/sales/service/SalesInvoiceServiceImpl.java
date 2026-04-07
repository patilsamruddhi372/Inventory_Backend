package com.Inventory.Inventory_Backend.sales.service;

import com.Inventory.Inventory_Backend.item.entity.Item;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import com.Inventory.Inventory_Backend.party.entity.PartyType; // ✅ ADDED
import com.Inventory.Inventory_Backend.sales.common.exception.InsufficientStockException;
import com.Inventory.Inventory_Backend.sales.common.exception.ResourceNotFoundException;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceItemDTO;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceRequestDTO;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceResponseDTO;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoiceItem;
import com.Inventory.Inventory_Backend.sales.mapper.SalesMapper;
import com.Inventory.Inventory_Backend.sales.repository.SalesInvoiceRepository;
import com.Inventory.Inventory_Backend.stock.dto.StockResponseDTO;
import com.Inventory.Inventory_Backend.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

    private final SalesInvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final PartyRepository partyRepository;
    private final SalesMapper salesMapper;
    private final StockService stockService;

    // ================= CREATE =================
    @Override
    @Transactional
    public SalesInvoiceResponseDTO createSalesInvoice(Long businessId,
                                                      SalesInvoiceRequestDTO request) {

        validatePartyExists(businessId, request.getPartyId());

        String invoiceNumber = generateInvoiceNumber(businessId);

        SalesInvoice invoice = SalesInvoice.builder()
                .businessId(businessId)
                .partyId(request.getPartyId())
                .invoiceNumber(invoiceNumber)
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .paymentType(request.getPaymentType())
                .amountPaid(safe(request.getAmountPaid()))
                .build();

        processItems(invoice, request.getItems(), request.isInterState(), businessId);
        calculateInvoiceTotals(invoice);

        SalesInvoice saved = invoiceRepository.save(invoice);

        for (SalesInvoiceItem item : saved.getItems()) {
            stockService.decreaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        SalesInvoiceResponseDTO response = salesMapper.toResponseDTO(saved);

        boolean eWayBillRequired =
                saved.getGrandTotal().compareTo(new BigDecimal("50000")) > 0;

        response.setEWayBillRequired(eWayBillRequired);

        return response;
    }

    // ================= GET =================
    @Override
    public List<SalesInvoiceResponseDTO> getAllSalesInvoices(Long businessId) {

        return invoiceRepository
                .findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(businessId)
                .stream()
                .map(salesMapper::toResponseDTO)
                .toList();
    }

    @Override
    public SalesInvoiceResponseDTO getSalesInvoiceById(Long businessId, Long invoiceId) {

        return salesMapper.toResponseDTO(findActiveInvoice(businessId, invoiceId));
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public SalesInvoiceResponseDTO updateSalesInvoice(Long businessId,
                                                      Long invoiceId,
                                                      SalesInvoiceRequestDTO request) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        validatePartyExists(businessId, request.getPartyId());

        restoreStockForItems(invoice.getItems(), businessId);
        invoice.getItems().clear();

        invoice.setPartyId(request.getPartyId());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setPaymentType(request.getPaymentType());
        invoice.setAmountPaid(safe(request.getAmountPaid()));

        processItems(invoice, request.getItems(), request.isInterState(), businessId);
        calculateInvoiceTotals(invoice);

        SalesInvoice saved = invoiceRepository.save(invoice);

        for (SalesInvoiceItem item : saved.getItems()) {
            stockService.decreaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        return salesMapper.toResponseDTO(saved);
    }

    // ================= DELETE =================
    @Override
    @Transactional
    public void deleteSalesInvoice(Long businessId, Long invoiceId) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        if (invoice.getAmountPaid() != null &&
                invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {

            throw new RuntimeException("Cannot delete paid or partially paid invoice");
        }

        if ("paid".equalsIgnoreCase(invoice.getStatus()) ||
                "partial".equalsIgnoreCase(invoice.getStatus())) {

            throw new RuntimeException("Cannot delete paid or partially paid invoice");
        }

        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
    }

    // ================= CANCEL =================
    @Override
    @Transactional
    public SalesInvoiceResponseDTO cancelInvoice(Long businessId, Long invoiceId) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        restoreStockForItems(invoice.getItems(), businessId);
        invoice.setStatus("CANCELLED");

        return salesMapper.toResponseDTO(invoiceRepository.save(invoice));
    }

    // ================= VALIDATION =================
    private void validatePartyExists(Long businessId, Long partyId) {

        var party = partyRepository
                .findByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found: " + partyId));

        if (!party.getBusinessId().equals(businessId) || !party.getIsActive()) {
            throw new ResourceNotFoundException("Customer not found: " + partyId);
        }

        // ✅ ENUM FIX (MAIN ERROR SOLVED)
        if (party.getType() != PartyType.CUSTOMER &&
                party.getType() != PartyType.BOTH) {

            throw new RuntimeException("Only CUSTOMER or BOTH type allowed for sales");
        }
    }

    private SalesInvoice findActiveInvoice(Long businessId, Long invoiceId) {

        return invoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(invoiceId, businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sales invoice not found: " + invoiceId));
    }

    private String generateInvoiceNumber(Long businessId) {

        Optional<String> latest =
                invoiceRepository.findLatestInvoiceNumberByBusinessId(businessId);

        long next = latest
                .map(num -> Long.parseLong(num.replace("INV-", "")) + 1)
                .orElse(1L);

        return String.format("INV-%06d", next);
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // ================= STOCK =================
    private void restoreStockForItems(List<SalesInvoiceItem> items, Long businessId) {

        for (SalesInvoiceItem item : items) {
            stockService.increaseStock(
                    businessId,
                    item.getItemId(),
                    item.getQuantity(),
                    null
            );
        }
    }

    private void processItems(SalesInvoice invoice,
                              List<SalesInvoiceItemDTO> items,
                              boolean interState,
                              Long businessId) {

        for (SalesInvoiceItemDTO dto : items) {

            Item item = itemRepository
                    .findByIdAndBusinessId(dto.getItemId(), businessId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Item not found"));

            StockResponseDTO stock = stockService.getStock(businessId, dto.getItemId());

            BigDecimal availableQty = stock != null
                    ? stock.getQuantity()
                    : BigDecimal.ZERO;

            if (availableQty.compareTo(dto.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for item: " + item.getName());
            }

            SalesInvoiceItem entity = SalesInvoiceItem.builder()
                    .businessId(businessId)
                    .salesInvoice(invoice)
                    .itemId(dto.getItemId())
                    .quantity(dto.getQuantity())
                    .rate(dto.getRate())
                    .unit(dto.getUnit())
                    .discount(safe(dto.getDiscount()))
                    .gstRate(safe(dto.getGstRate()))
                    .build();

            invoice.addItem(entity);
        }
    }

    private void calculateInvoiceTotals(SalesInvoice invoice) {

        BigDecimal total = BigDecimal.ZERO;

        for (SalesInvoiceItem item : invoice.getItems()) {
            total = total.add(item.getQuantity().multiply(item.getRate()));
        }

        invoice.setGrandTotal(total);
        invoice.setBalance(total.subtract(safe(invoice.getAmountPaid())));
    }
}