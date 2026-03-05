package com.Inventory.Inventory_Backend.purchase.mapper;

import com.Inventory.Inventory_Backend.purchase.dto.*;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoice;
import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoiceItem;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PurchaseInvoiceMapper {

    /**
     * Request DTO → Entity
     * businessId is NOT set here (comes from BusinessContext in service)
     * ✅ Items list initialized as empty ArrayList — items added in service
     */
    public PurchaseInvoice toEntity(PurchaseInvoiceRequestDTO dto) {

        String billNumber = dto.getBillNumber() != null
                ? dto.getBillNumber().trim()
                : null;

        return PurchaseInvoice.builder()
                .partyId(dto.getPartyId())
                .billNumber(billNumber)
                .billDate(dto.getBillDate())
                .dueDate(dto.getDueDate())
                .amountPaid(
                        dto.getAmountPaid() != null
                                ? dto.getAmountPaid()
                                : BigDecimal.ZERO
                )
                .notes(dto.getNotes())
                .items(new ArrayList<>())    // ✅ Explicit empty list
                .build();
    }

    /**
     * Item DTO → Item Entity
     * ✅ Does NOT set purchaseInvoice or businessId
     *    Those are set by invoice.addItem() in the service
     */
    public PurchaseInvoiceItem toItemEntity(PurchaseInvoiceItemDTO dto) {

        BigDecimal qty = dto.getQuantity();
        BigDecimal rate = dto.getRate();
        BigDecimal gst = dto.getGstRate() != null
                ? dto.getGstRate()
                : BigDecimal.ZERO;

        BigDecimal lineBase = qty.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal lineTax = lineBase.multiply(gst)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal lineTotal = lineBase.add(lineTax);

        return PurchaseInvoiceItem.builder()
                .itemId(dto.getItemId())
                .quantity(qty)
                .unit(dto.getUnit())
                .rate(rate)
                .gstRate(gst)
                .total(lineTotal)
                .isDeleted(false)       // ✅ Explicit default
                .build();
    }

    /**
     * Entity → Response DTO
     */
    public PurchaseInvoiceResponseDTO toResponseDTO(PurchaseInvoice entity) {

        List<PurchaseInvoiceItemResponseDTO> itemDTOs;

        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            itemDTOs = entity.getItems()
                    .stream()
                    .map(this::toItemResponseDTO)
                    .toList();
        } else {
            itemDTOs = Collections.emptyList();
        }

        // ✅ Safely access lazy-loaded party
        String partyName = getPartyNameSafely(entity);

        return PurchaseInvoiceResponseDTO.builder()
                .id(entity.getId())
                .businessId(entity.getBusinessId())
                .partyId(entity.getPartyId())
                .partyName(partyName)
                .billNumber(entity.getBillNumber())
                .billDate(entity.getBillDate())
                .dueDate(entity.getDueDate())
                .subtotal(entity.getSubtotal())
                .totalTax(entity.getTotalTax())
                .grandTotal(entity.getGrandTotal())
                .amountPaid(entity.getAmountPaid())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(itemDTOs)
                .totalItems(itemDTOs.size())
                .build();
    }

    /**
     * Item Entity → Item Response DTO
     */
    public PurchaseInvoiceItemResponseDTO toItemResponseDTO(
            PurchaseInvoiceItem entity) {

        // ✅ Safely access lazy-loaded item
        String itemName = getItemNameSafely(entity);

        BigDecimal gstRate = entity.getGstRate() != null
                ? entity.getGstRate()
                : BigDecimal.ZERO;

        BigDecimal lineBase = entity.getQuantity()
                .multiply(entity.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = lineBase
                .multiply(gstRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return PurchaseInvoiceItemResponseDTO.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .itemName(itemName)
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .rate(entity.getRate())
                .gstRate(gstRate)
                .taxAmount(taxAmount)
                .total(entity.getTotal())
                .build();
    }

    /**
     * Entity list → Response DTO list
     */
    public List<PurchaseInvoiceResponseDTO> toResponseDTOList(
            List<PurchaseInvoice> entities) {

        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * ✅ Safely access lazy-loaded party name
     *    Catches LazyInitializationException instead of generic Exception
     */
    private String getPartyNameSafely(PurchaseInvoice entity) {
        try {
            if (entity.getParty() != null) {
                return entity.getParty().getName();
            }
        } catch (LazyInitializationException e) {
            log.debug("Party not loaded for invoice id={}", entity.getId());
        }
        return null;
    }

    /**
     * ✅ Safely access lazy-loaded item name
     *    Catches LazyInitializationException instead of generic Exception
     */
    private String getItemNameSafely(PurchaseInvoiceItem entity) {
        try {
            if (entity.getItem() != null) {
                return entity.getItem().getName();
            }
        } catch (LazyInitializationException e) {
            log.debug("Item not loaded for invoice item id={}", entity.getId());
        }
        return null;
    }
}