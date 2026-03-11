package com.Inventory.Inventory_Backend.quotation.service.impl;

import com.Inventory.Inventory_Backend.item.entity.Item;
import com.Inventory.Inventory_Backend.item.repository.ItemRepository;
import com.Inventory.Inventory_Backend.party.repository.PartyRepository;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationItemDTO;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationRequestDTO;
import com.Inventory.Inventory_Backend.quotation.dto.QuotationResponseDTO;
import com.Inventory.Inventory_Backend.quotation.entity.Quotation;
import com.Inventory.Inventory_Backend.quotation.entity.QuotationItem;
import com.Inventory.Inventory_Backend.quotation.repository.QuotationItemRepository;
import com.Inventory.Inventory_Backend.quotation.repository.QuotationRepository;
import com.Inventory.Inventory_Backend.quotation.service.QuotationService;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoiceItem;
import com.Inventory.Inventory_Backend.sales.repository.SalesInvoiceRepository;
import com.Inventory.Inventory_Backend.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final PartyRepository partyRepository;
    private final ItemRepository itemRepository;
    private final SalesInvoiceRepository salesInvoiceRepository;
    private final StockService stockService;

    // ============================================================
    // CREATE QUOTATION
    // ============================================================

    @Override
    @Transactional
    public QuotationResponseDTO createQuotation(Long businessId, QuotationRequestDTO request) {

        validateParty(businessId, request.getPartyId());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        Quotation quotation = Quotation.builder()
                .businessId(businessId)
                .quotationNumber(generateQuotationNumber())
                .partyId(request.getPartyId())
                .quotationDate(request.getQuotationDate())
                .validUntil(request.getValidUntil())
                .discountAmount(request.getDiscountAmount())
                .shippingCharges(request.getShippingCharges())
                .notes(request.getNotes())
                .paymentTerms(request.getPaymentTerms())
                .deliveryTime(request.getDeliveryTime())
                .termsAndConditions(request.getTermsAndConditions())
                .status("DRAFT")
                .isDeleted(false)
                .build();

        Quotation savedQuotation = quotationRepository.save(quotation);

        List<QuotationItem> items = new ArrayList<>();

        for (QuotationItemDTO dto : request.getItems()) {

            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + dto.getItemId()));

            BigDecimal quantity = dto.getQuantity();
            BigDecimal rate = dto.getRate();

            BigDecimal amount = quantity.multiply(rate);

            BigDecimal gstRate = dto.getGstRate() == null ? BigDecimal.ZERO : dto.getGstRate();

            BigDecimal taxAmount = amount.multiply(gstRate)
                    .divide(BigDecimal.valueOf(100));

            subtotal = subtotal.add(amount);
            taxTotal = taxTotal.add(taxAmount);

            QuotationItem quotationItem = QuotationItem.builder()
                    .businessId(businessId)
                    .quotationId(savedQuotation.getId())
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .description(dto.getDescription())
                    .quantity(quantity)
                    .unit(dto.getUnit())
                    .rate(rate)
                    .gstRate(gstRate)
                    .taxAmount(taxAmount)
                    .amount(amount)
                    .hsnCode(dto.getHsnCode())
                    .build();

            items.add(quotationItem);
        }

        quotationItemRepository.saveAll(items);

        BigDecimal shipping = request.getShippingCharges() == null ? BigDecimal.ZERO : request.getShippingCharges();
        BigDecimal discount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();

        BigDecimal total = subtotal.add(taxTotal).add(shipping).subtract(discount);

        savedQuotation.setSubtotal(subtotal);
        savedQuotation.setTaxAmount(taxTotal);
        savedQuotation.setTotalAmount(total);

        quotationRepository.save(savedQuotation);

        return mapToResponseDTO(savedQuotation);
    }

    // ============================================================
    // CONVERT QUOTATION → SALES INVOICE
    // ============================================================

    @Override
    @Transactional
    public Long convertToSalesInvoice(Long businessId, Long quotationId) {

        Quotation quotation = quotationRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(quotationId, businessId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        if ("CONVERTED".equals(quotation.getStatus())) {
            throw new RuntimeException("Quotation already converted");
        }

        List<QuotationItem> quotationItems =
                quotationItemRepository.findByQuotationId(quotationId);

        SalesInvoice invoice = SalesInvoice.builder()
                .businessId(businessId)
                .partyId(quotation.getPartyId())
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDate.now())
                .amountPaid(BigDecimal.ZERO)
                .build();

        List<SalesInvoiceItem> items = new ArrayList<>();

        for (QuotationItem qi : quotationItems) {

            SalesInvoiceItem item = SalesInvoiceItem.builder()
                    .businessId(businessId)
                    .salesInvoice(invoice)
                    .itemId(qi.getItemId())
                    .quantity(qi.getQuantity())
                    .rate(qi.getRate())
                    .total(qi.getAmount())
                    .build();

            items.add(item);

            // 🔹 STOCK REDUCTION
            stockService.decreaseStockFromQuotation(
                    businessId,
                    qi.getItemId(),
                    qi.getQuantity(),
                    quotationId
            );
        }

        invoice.setItems(items);

        SalesInvoice savedInvoice = salesInvoiceRepository.save(invoice);

        quotation.setStatus("CONVERTED");
        quotation.setConvertedToInvoiceId(savedInvoice.getId());
        quotation.setConvertedAt(LocalDateTime.now());

        quotationRepository.save(quotation);

        return savedInvoice.getId();
    }

    // ============================================================
    // GET ALL QUOTATIONS
    // ============================================================

    @Override
    public List<QuotationResponseDTO> getAllQuotations(Long businessId) {

        return quotationRepository
                .findByBusinessIdAndIsDeletedFalse(businessId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ============================================================
    // GET SINGLE QUOTATION
    // ============================================================

    @Override
    public QuotationResponseDTO getQuotationById(Long businessId, Long quotationId) {

        Quotation quotation = quotationRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(quotationId, businessId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        return mapToResponseDTO(quotation);
    }

    // ============================================================
    // DELETE QUOTATION (SOFT DELETE)
    // ============================================================

    @Override
    @Transactional
    public void deleteQuotation(Long businessId, Long quotationId) {

        Quotation quotation = quotationRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(quotationId, businessId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        if ("CONVERTED".equals(quotation.getStatus())) {
            throw new RuntimeException("Cannot delete converted quotation");
        }

        quotation.setIsDeleted(true);

        quotationRepository.save(quotation);
    }

    // ============================================================
    // VALIDATE PARTY
    // ============================================================

    private void validateParty(Long businessId, Long partyId) {

        if (!partyRepository.existsByIdAndBusinessIdAndIsActiveTrue(partyId, businessId)) {
            throw new RuntimeException("Customer not found: " + partyId);
        }
    }

    // ============================================================
    // NUMBER GENERATORS
    // ============================================================

    private String generateQuotationNumber() {
        return "QT-" + System.currentTimeMillis();
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    // ============================================================
    // ENTITY → DTO
    // ============================================================

    private QuotationResponseDTO mapToResponseDTO(Quotation quotation) {

        QuotationResponseDTO dto = new QuotationResponseDTO();

        dto.setId(quotation.getId());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setPartyId(quotation.getPartyId());
        dto.setQuotationDate(quotation.getQuotationDate());
        dto.setValidUntil(quotation.getValidUntil());
        dto.setSubtotal(quotation.getSubtotal());
        dto.setTaxAmount(quotation.getTaxAmount());
        dto.setDiscountAmount(quotation.getDiscountAmount());
        dto.setShippingCharges(quotation.getShippingCharges());
        dto.setTotalAmount(quotation.getTotalAmount());
        dto.setStatus(quotation.getStatus());

        List<QuotationItemDTO> itemDTOs =
                quotationItemRepository.findByQuotationId(quotation.getId())
                        .stream()
                        .map(item -> {

                            QuotationItemDTO itemDTO = new QuotationItemDTO();

                            itemDTO.setItemId(item.getItemId());
                            itemDTO.setItemName(item.getItemName());
                            itemDTO.setDescription(item.getDescription());
                            itemDTO.setQuantity(item.getQuantity());
                            itemDTO.setUnit(item.getUnit());
                            itemDTO.setRate(item.getRate());
                            itemDTO.setDiscountPercent(item.getDiscountPercent());
                            itemDTO.setDiscountAmount(item.getDiscountAmount());
                            itemDTO.setGstRate(item.getGstRate());
                            itemDTO.setTaxAmount(item.getTaxAmount());
                            itemDTO.setAmount(item.getAmount());
                            itemDTO.setHsnCode(item.getHsnCode());

                            return itemDTO;
                        })
                        .toList();

        dto.setItems(itemDTOs);

        return dto;
    }
}