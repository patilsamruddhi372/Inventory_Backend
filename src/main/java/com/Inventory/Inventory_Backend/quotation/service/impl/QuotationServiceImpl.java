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
        BigDecimal totalItemDiscount = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        Quotation quotation = Quotation.builder()
                .businessId(businessId)
                .quotationNumber(generateQuotationNumber())
                .partyId(request.getPartyId())
                .quotationDate(request.getQuotationDate())
                .validUntil(request.getValidUntil())
                .discountAmount(request.getDiscountAmount()) // Global discount
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

            Item item = itemRepository
                    .findByIdAndBusinessId(dto.getItemId(), businessId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + dto.getItemId()));

            BigDecimal quantity = dto.getQuantity();
            BigDecimal rate = dto.getRate();

            // 1. Line Gross Amount
            BigDecimal lineGrossAmount = quantity.multiply(rate);

            // 2. Line Discount
            BigDecimal discountAmt = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;

            // 3. Taxable Amount
            BigDecimal taxableAmount = lineGrossAmount.subtract(discountAmt);

            // 4. Tax
            BigDecimal gstRate = dto.getGstRate() == null ? BigDecimal.ZERO : dto.getGstRate();
            BigDecimal taxAmount = taxableAmount.multiply(gstRate)
                    .divide(BigDecimal.valueOf(100));

            // Accumulate totals
            subtotal = subtotal.add(lineGrossAmount);
            totalItemDiscount = totalItemDiscount.add(discountAmt);
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
                    .discountPercent(dto.getDiscountPercent())
                    .discountAmount(discountAmt)
                    .gstRate(gstRate)
                    .taxAmount(taxAmount)

                    // Store line net amount (Taxable) OR (Taxable + Tax)?
                    // Usually line amount = Taxable Amount
                    .amount(taxableAmount)

                    .hsnCode(dto.getHsnCode())
                    .build();

            items.add(quotationItem);
        }

        savedQuotation.setItems(items);

        quotationItemRepository.saveAll(items);

        BigDecimal shipping = request.getShippingCharges() == null ? BigDecimal.ZERO : request.getShippingCharges();
        BigDecimal globalDiscount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();

        // Final Total Calculation
        // (Subtotal - ItemDiscounts) + Tax + Shipping - GlobalDiscount

        BigDecimal grandTotal = subtotal
                .subtract(totalItemDiscount)
                .add(taxTotal)
                .add(shipping)
                .subtract(globalDiscount);

        // Store totals
        savedQuotation.setSubtotal(subtotal);
        savedQuotation.setTaxAmount(taxTotal);

        // IMPORTANT: Store total discount (Item Discounts + Global Discount)
        savedQuotation.setDiscountAmount(totalItemDiscount.add(globalDiscount));

        savedQuotation.setTotalAmount(grandTotal);

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

        if("CONVERTED".equals(quotation.getStatus())){
            throw new RuntimeException("Quotation already converted");
        }

        if(!"APPROVED".equals(quotation.getStatus())){
            throw new RuntimeException("Only approved quotations can be converted");
        }

        List<QuotationItem> quotationItems =
                quotationItemRepository.findByQuotationIdAndBusinessId(quotationId, businessId);

        //security check
        for(QuotationItem qi : quotationItems){
            if(!qi.getBusinessId().equals(businessId)){
                throw new RuntimeException("Cross-business data access detected");
            }
        }

        //1. create fully populated invoice
        SalesInvoice invoice = SalesInvoice.builder()
                .businessId(businessId)
                .partyId(quotation.getPartyId())
                .invoiceNumber(generateInvoiceNumber())
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .paymentType("CREDIT")
                .quotationId(quotationId)
                .amountPaid(BigDecimal.ZERO)
                .subtotal(quotation.getSubtotal())
                .totalDiscount(quotation.getDiscountAmount())
                .totalTax(quotation.getTaxAmount())
                .grandTotal(quotation.getTotalAmount())
                .balance(quotation.getTotalAmount())

                .status("pending")
                .isDeleted(false)
                .build();

        List<SalesInvoiceItem> items = new ArrayList<>();

        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;

        for(QuotationItem qi : quotationItems){
            BigDecimal gstRate = qi.getGstRate() != null ? qi.getGstRate() : BigDecimal.ZERO;
            BigDecimal taxAmount = qi.getTaxAmount() != null ? qi.getTaxAmount() : BigDecimal.ZERO;

            //split tax intocgst/sgst(50% each)
            BigDecimal cgst = taxAmount.divide(BigDecimal.valueOf(2));
            BigDecimal sgst = taxAmount.divide(BigDecimal.valueOf(2));
            BigDecimal igst = BigDecimal.ZERO;

            totalCgst = totalCgst.add(cgst);
            totalSgst = totalSgst.add(sgst);

            SalesInvoiceItem item = SalesInvoiceItem.builder()
                    .businessId(businessId)
                    .salesInvoice(invoice)
                    .itemId(qi.getItemId())
                    .quantity(qi.getQuantity())
                    .unit(qi.getUnit())
                    .rate(qi.getRate())
                    .discount(qi.getDiscountAmount())
                    .gstRate(gstRate)
                    .cgstAmount(cgst)
                    .sgstAmount(sgst)
                    .igstAmount(igst)
                    .total(qi.getAmount().add(taxAmount))
                    .build();

            items.add(item);

            stockService.decreaseStockFromQuotation(
                    businessId,
                    qi.getItemId(),
                    qi.getQuantity(),
                    quotationId
            );
        }

        invoice.setItems(items);

        invoice.setTotalCgst(totalCgst);
        invoice.setTotalSgst(totalSgst);
        invoice.setTotalIgst(totalIgst);

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
    // UPDATE QUOTATION STATUS
    // ============================================================

    @Override
    @Transactional
    public QuotationResponseDTO updateQuotationStatus(Long businessId,
            Long quotationId,
            String status) {

        Quotation quotation = quotationRepository
                .findByIdAndBusinessIdAndIsDeletedFalse(quotationId, businessId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        if ("CONVERTED".equals(quotation.getStatus())) {
            throw new RuntimeException("Cannot change status of converted quotation");
        }

        if (status == null || status.isBlank()) {
            throw new RuntimeException("Status is required");
        }

        String normalizedStatus = status.trim().toUpperCase();

        List<String> allowedStatuses = List.of(
                "DRAFT",
                "SENT",
                "APPROVED",
                "REJECTED");

        if (!allowedStatuses.contains(normalizedStatus)) {
            throw new RuntimeException("Invalid quotation status: " + status);
        }

        quotation.setStatus(normalizedStatus);

        Quotation savedQuotation = quotationRepository.save(quotation);

        return mapToResponseDTO(savedQuotation);
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
        dto.setPaymentTerms(quotation.getPaymentTerms());
        dto.setDeliveryTime(quotation.getDeliveryTime());
        dto.setNotes(quotation.getNotes());
        dto.setStatus(quotation.getStatus());

        List<QuotationItemDTO> itemDTOs = quotationItemRepository.findByQuotationIdAndBusinessId(quotation.getId(), quotation.getBusinessId())
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