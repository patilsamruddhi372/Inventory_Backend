package com.Inventory.Inventory_Backend.quotation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotation_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "quotation_id", nullable = false)
    private Long quotationId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal rate;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "gst_rate")
    private BigDecimal gstRate;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    private BigDecimal amount;

    @Column(name = "hsn_code")
    private String hsnCode;

    // ============================================================
    // AUDIT
    // ============================================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ============================================================
    // AUTO TIMESTAMP
    // ============================================================

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        if (discountPercent == null) discountPercent = BigDecimal.ZERO;
        if (discountAmount == null) discountAmount = BigDecimal.ZERO;
        if (gstRate == null) gstRate = BigDecimal.ZERO;
        if (taxAmount == null) taxAmount = BigDecimal.ZERO;
        if (amount == null) amount = BigDecimal.ZERO;
    }
}