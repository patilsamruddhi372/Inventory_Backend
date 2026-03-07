package com.Inventory.Inventory_Backend.sales.mapper;

import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceItemResponseDTO;
import com.Inventory.Inventory_Backend.sales.dto.SalesInvoiceResponseDTO;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import com.Inventory.Inventory_Backend.sales.entity.SalesInvoiceItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SalesMapper {

    // ==========================================================
    // SALES INVOICE → RESPONSE DTO
    // ==========================================================
    public SalesInvoiceResponseDTO toResponseDTO(SalesInvoice entity) {

        if (entity == null) {
            return null;
        }

        List<SalesInvoiceItemResponseDTO> items =
                entity.getItems() == null
                        ? Collections.emptyList()
                        : entity.getItems()
                        .stream()
                        .map(this::toItemResponseDTO)
                        .toList();

        return SalesInvoiceResponseDTO.builder()
                .id(entity.getId())
                .businessId(entity.getBusinessId())
                .partyId(entity.getPartyId())
                .invoiceNumber(entity.getInvoiceNumber())
                .invoiceDate(entity.getInvoiceDate())
                .dueDate(entity.getDueDate())

                // payment type ✅ ADDED
                .paymentType(entity.getPaymentType())

                // financial
                .subtotal(entity.getSubtotal())
                .totalDiscount(entity.getTotalDiscount())
                .totalCgst(entity.getTotalCgst())
                .totalSgst(entity.getTotalSgst())
                .totalIgst(entity.getTotalIgst())
                .totalTax(entity.getTotalTax())

                .grandTotal(entity.getGrandTotal())
                .amountPaid(entity.getAmountPaid())
                .balance(entity.getBalance())

                // status
                .status(entity.getStatus())

                // timestamps
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                // items
                .items(items)

                .build();
    }

    // ==========================================================
    // SALES INVOICE ITEM → RESPONSE DTO
    // ==========================================================
    public SalesInvoiceItemResponseDTO toItemResponseDTO(SalesInvoiceItem entity) {

        if (entity == null) {
            return null;
        }

        return SalesInvoiceItemResponseDTO.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .rate(entity.getRate())
                .discount(entity.getDiscount())
                .gstRate(entity.getGstRate())
                .cgstAmount(entity.getCgstAmount())
                .sgstAmount(entity.getSgstAmount())
                .igstAmount(entity.getIgstAmount())
                .total(entity.getTotal())
                .build();
    }
}