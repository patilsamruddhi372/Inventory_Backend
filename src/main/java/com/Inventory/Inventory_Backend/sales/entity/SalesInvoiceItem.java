package com.Inventory.Inventory_Backend.sales.entity;

import com.Inventory.Inventory_Backend.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "sales_invoice_items",
        indexes = {
                @Index(name = "idx_sales_item_invoice", columnList = "sales_invoice_id"),
                @Index(name = "idx_sales_item_item", columnList = "item_id"),
                @Index(name = "idx_sales_item_business", columnList = "business_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"salesInvoice", "item"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SalesInvoiceItem {

    // ==========================================================
    // PRIMARY KEY
    // ==========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // ==========================================================
    // BUSINESS (MULTI-TENANT)
    // ==========================================================

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    // ==========================================================
    // INVOICE RELATIONSHIP
    // ==========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sales_invoice_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_sales_item_invoice")
    )
    private SalesInvoice salesInvoice;

    // ==========================================================
    // ITEM
    // ==========================================================

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    // ==========================================================
    // ITEM DETAILS
    // ==========================================================

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(length = 20)
    private String unit;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal rate;

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    // ==========================================================
    // TAX
    // ==========================================================

    @Builder.Default
    @Column(name = "gst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal gstRate = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "cgst_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "sgst_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "igst_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    // ==========================================================
    // TOTAL
    // ==========================================================

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
}