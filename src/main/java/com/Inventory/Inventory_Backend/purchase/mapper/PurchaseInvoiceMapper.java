package com.Inventory.Inventory_Backend.purchase.mapper;

import com.Inventory.Inventory_Backend.purchase.dto.*;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoice;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoiceItem;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PurchaseInvoiceMapper {

    // =========================================================
    // REQUEST DTO → ENTITY
    // =========================================================
    public PurchaseInvoice toEntity(PurchaseInvoiceRequestDTO dto) {

        String billNumber = dto.getBillNumber() != null
                ? dto.getBillNumber().trim()
                : null;

        return PurchaseInvoice.builder()
                .partyId(dto.getPartyId())
                .billNumber(billNumber)
                .billDate(dto.getBillDate())
                .dueDate(dto.getDueDate())
                .paymentType(dto.getPaymentType())   // ✅ ADDED
                .amountPaid(dto.getAmountPaid() != null
                        ? dto.getAmountPaid()
                        : BigDecimal.ZERO)
                .notes(dto.getNotes())
                .build();
    }

    // =========================================================
    // ITEM DTO → ITEM ENTITY
    // =========================================================
    public PurchaseInvoiceItem toItemEntity(PurchaseInvoiceItemDTO dto) {

        BigDecimal quantity = dto.getQuantity();
        BigDecimal rate = dto.getRate();
        BigDecimal gstRate = dto.getGstRate() != null
                ? dto.getGstRate()
                : BigDecimal.ZERO;

        BigDecimal base = quantity.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = base.multiply(gstRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = base.add(tax);

        return PurchaseInvoiceItem.builder()
                .itemId(dto.getItemId())
                .quantity(quantity)
                .unit(dto.getUnit())
                .rate(rate)
                .gstRate(gstRate)
                .total(total)
                .build();
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================
    public PurchaseInvoiceResponseDTO toResponseDTO(PurchaseInvoice entity) {

        List<PurchaseInvoiceItemResponseDTO> items =
                entity.getItems() == null
                        ? Collections.emptyList()
                        : entity.getItems()
                        .stream()
                        .map(this::toItemResponseDTO)
                        .toList();

        return PurchaseInvoiceResponseDTO.builder()
                .id(entity.getId())
                .businessId(entity.getBusinessId())
                .partyId(entity.getPartyId())
                .partyName(getPartyNameSafely(entity))
                .billNumber(entity.getBillNumber())
                .billDate(entity.getBillDate())
                .dueDate(entity.getDueDate())

                .subtotal(entity.getSubtotal())
                .totalTax(entity.getTotalTax())
                .grandTotal(entity.getGrandTotal())

                .paymentType(entity.getPaymentType())   // ✅ ADDED

                .amountPaid(entity.getAmountPaid())
                .balance(entity.getBalance())

                .status(entity.getStatus())
                .notes(entity.getNotes())

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .items(items)
                .totalItems(items.size())
                .build();
    }

    // =========================================================
    // ITEM ENTITY → ITEM RESPONSE DTO
    // =========================================================
    public PurchaseInvoiceItemResponseDTO toItemResponseDTO(PurchaseInvoiceItem entity) {

        BigDecimal gstRate = entity.getGstRate() != null
                ? entity.getGstRate()
                : BigDecimal.ZERO;

        BigDecimal base = entity.getQuantity()
                .multiply(entity.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = base.multiply(gstRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return PurchaseInvoiceItemResponseDTO.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .itemName(getItemNameSafely(entity))
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .rate(entity.getRate())
                .gstRate(gstRate)
                .taxAmount(tax)
                .total(entity.getTotal())
                .build();
    }

    // =========================================================
    // ENTITY LIST → RESPONSE DTO LIST
    // =========================================================
    public List<PurchaseInvoiceResponseDTO> toResponseDTOList(List<PurchaseInvoice> entities) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =========================================================
    // SAFE LAZY ACCESS HELPERS
    // =========================================================

    private String getPartyNameSafely(PurchaseInvoice entity) {
        try {
            return entity.getParty() != null
                    ? entity.getParty().getName()
                    : null;
        } catch (LazyInitializationException e) {
            log.debug("Party not initialized for invoice {}", entity.getId());
            return null;
        }
    }

    private String getItemNameSafely(PurchaseInvoiceItem entity) {
        try {
            return entity.getItem() != null
                    ? entity.getItem().getName()
                    : null;
        } catch (LazyInitializationException e) {
            log.debug("Item not initialized for purchase item {}", entity.getId());
            return null;
        }
    }
}