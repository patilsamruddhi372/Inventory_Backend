package com.Inventory.Inventory_Backend.sales.service;

import com.Inventory.Inventory_Backend.item.entity.Item;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
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
    private final com.Inventory.Inventory_Backend.ewaybill.service.EWayBillService eWayBillService;

    // ============================================================
    // CREATE SALES INVOICE
    // ============================================================

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
                    saved.getBusinessId(),
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        // ================================================================
        // AUTO GENERATE EWAY BILL IF REQUIRED
        // ================================================================
        try {
            eWayBillService.generateIfRequired(saved);
        } catch (Exception e) {
            log.error("Failed to auto generate EWayBill for invoice {}", saved.getId(), e);
        }

        return salesMapper.toResponseDTO(saved);
    }

    // ============================================================
    // GET ALL SALES INVOICES
    // ============================================================

    @Override
    public List<SalesInvoiceResponseDTO> getAllSalesInvoices(Long businessId) {

        return invoiceRepository
                .findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(businessId)
                .stream()
                .map(salesMapper::toResponseDTO)
                .toList();
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    public SalesInvoiceResponseDTO getSalesInvoiceById(Long businessId, Long invoiceId) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        return salesMapper.toResponseDTO(invoice);
    }

    // ============================================================
    // UPDATE INVOICE
    // ============================================================

    @Override
    @Transactional
    public SalesInvoiceResponseDTO updateSalesInvoice(Long businessId,
                                                      Long invoiceId,
                                                      SalesInvoiceRequestDTO request) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        validatePartyExists(businessId, request.getPartyId());

        restoreStockForItems(invoice.getItems());

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
                    saved.getBusinessId(),
                    item.getItemId(),
                    item.getQuantity(),
                    saved.getId()
            );
        }

        return salesMapper.toResponseDTO(saved);
    }

    // ============================================================
    // DELETE INVOICE
    // ============================================================

    @Override
    @Transactional
    public void deleteSalesInvoice(Long businessId, Long invoiceId) {

        SalesInvoice invoice = findActiveInvoice(businessId, invoiceId);

        restoreStockForItems(invoice.getItems());

        invoice.setIsDeleted(true);
        invoice.setStatus("cancelled");

        invoiceRepository.save(invoice);
    }

    // ============================================================
    // PROCESS ITEMS
    // ============================================================

    private void processItems(SalesInvoice invoice,
                              List<SalesInvoiceItemDTO> itemDTOs,
                              boolean interState,
                              Long businessId) {

        for (SalesInvoiceItemDTO dto : itemDTOs) {

            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Item not found: " + dto.getItemId()));

            StockResponseDTO stock = stockService.getStock(businessId, dto.getItemId());

            BigDecimal availableStock =
                    stock != null ? stock.getQuantity() : BigDecimal.ZERO;

            if (availableStock.compareTo(dto.getQuantity()) < 0) {

                throw new InsufficientStockException(
                        "Insufficient stock for item: " + item.getName());
            }

            BigDecimal quantity = dto.getQuantity();
            BigDecimal rate = dto.getRate();
            BigDecimal discount = safe(dto.getDiscount());
            BigDecimal gstRate = safe(dto.getGstRate());

            BigDecimal lineAmount = quantity.multiply(rate);
            BigDecimal taxableAmount = lineAmount.subtract(discount);

            BigDecimal cgst = BigDecimal.ZERO;
            BigDecimal sgst = BigDecimal.ZERO;
            BigDecimal igst = BigDecimal.ZERO;

            if (interState) {

                igst = taxableAmount.multiply(gstRate)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            } else {

                cgst = taxableAmount.multiply(gstRate)
                        .divide(BigDecimal.valueOf(200), 2, RoundingMode.HALF_UP);

                sgst = cgst;
            }

            BigDecimal total = taxableAmount
                    .add(cgst)
                    .add(sgst)
                    .add(igst);

            SalesInvoiceItem lineItem = SalesInvoiceItem.builder()
                    .businessId(businessId)
                    .salesInvoice(invoice)   // IMPORTANT RELATION
                    .itemId(dto.getItemId())
                    .quantity(quantity)
                    .unit(dto.getUnit())
                    .rate(rate)
                    .discount(discount)
                    .gstRate(gstRate)
                    .cgstAmount(cgst)
                    .sgstAmount(sgst)
                    .igstAmount(igst)
                    .total(total)
                    .build();

            invoice.addItem(lineItem);
        }
    }

    // ============================================================
    // RESTORE STOCK
    // ============================================================

    private void restoreStockForItems(List<SalesInvoiceItem> items) {

        for (SalesInvoiceItem lineItem : items) {

            stockService.increaseStock(
                    lineItem.getBusinessId(),
                    lineItem.getItemId(),
                    lineItem.getQuantity(),
                    null
            );
        }
    }

    // ============================================================
    // CALCULATE TOTALS
    // ============================================================

    private void calculateInvoiceTotals(SalesInvoice invoice) {

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;

        for (SalesInvoiceItem item : invoice.getItems()) {

            subtotal = subtotal.add(item.getQuantity().multiply(item.getRate()));
            totalDiscount = totalDiscount.add(item.getDiscount());
            totalCgst = totalCgst.add(item.getCgstAmount());
            totalSgst = totalSgst.add(item.getSgstAmount());
            totalIgst = totalIgst.add(item.getIgstAmount());
        }

        BigDecimal totalTax = totalCgst.add(totalSgst).add(totalIgst);

        BigDecimal grandTotal = subtotal
                .subtract(totalDiscount)
                .add(totalTax);

        BigDecimal paid = safe(invoice.getAmountPaid());

        BigDecimal balance = grandTotal.subtract(paid);

        invoice.setSubtotal(subtotal);
        invoice.setTotalDiscount(totalDiscount);
        invoice.setTotalCgst(totalCgst);
        invoice.setTotalSgst(totalSgst);
        invoice.setTotalIgst(totalIgst);
        invoice.setTotalTax(totalTax);
        invoice.setGrandTotal(grandTotal);
        invoice.setBalance(balance);
        invoice.setStatus(determineStatus(paid, grandTotal));
    }

    // ============================================================
    // STATUS
    // ============================================================

    private String determineStatus(BigDecimal paid, BigDecimal total) {

        if (paid.compareTo(total) >= 0) return "paid";

        if (paid.compareTo(BigDecimal.ZERO) > 0) return "partial";

        return "pending";
    }

    // ============================================================
    // INVOICE NUMBER
    // ============================================================

    private String generateInvoiceNumber(Long businessId) {

        Optional<String> latest =
                invoiceRepository.findLatestInvoiceNumberByBusinessId(businessId);

        long next = latest
                .map(num -> Long.parseLong(num.replace("INV-", "")) + 1)
                .orElse(1L);

        return String.format("INV-%06d", next);
    }

    // ============================================================
    // VALIDATIONS
    // ============================================================

    private void validatePartyExists(Long businessId, Long partyId) {

        if (!partyRepository.existsByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)) {

            throw new ResourceNotFoundException("Customer not found: " + partyId);
        }
    }

    private SalesInvoice findActiveInvoice(Long businessId, Long invoiceId) {

        return invoiceRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(invoiceId, businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sales invoice not found: " + invoiceId));
    }

    private BigDecimal safe(BigDecimal value) {

        return value != null ? value : BigDecimal.ZERO;
    }
}