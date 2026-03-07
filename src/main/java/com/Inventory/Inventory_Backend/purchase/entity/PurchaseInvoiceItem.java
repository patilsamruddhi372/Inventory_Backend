package com.Inventory.Inventory_Backend.purchase.entity;

import com.Inventory.Inventory_Backend.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "purchase_invoice_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_invoice_item",
                        columnNames = {"purchase_invoice_id", "item_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"purchaseInvoice", "item"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseInvoiceItem {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // =========================================================
    // TENANT
    // =========================================================

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    // =========================================================
    // ITEM REFERENCE
    // =========================================================

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    // =========================================================
    // QUANTITY
    // =========================================================

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(length = 50)
    private String unit;

    // =========================================================
    // PRICE
    // =========================================================

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal rate;

    @Builder.Default
    @Column(name = "gst_rate", precision = 5, scale = 2)
    private BigDecimal gstRate = BigDecimal.ZERO;

    // =========================================================
    // TOTAL
    // =========================================================

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    // =========================================================
    // RELATIONSHIPS
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_invoice_id", nullable = false)
    private PurchaseInvoice purchaseInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "item_id",
            insertable = false,
            updatable = false
    )
    private Item item;
}