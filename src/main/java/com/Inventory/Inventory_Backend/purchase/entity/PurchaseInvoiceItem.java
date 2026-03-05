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

// ✅ REMOVED @SQLDelete  → orphanRemoval now does REAL DELETE
// ✅ REMOVED @Where       → Hibernate sees all items in collection

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"purchaseInvoice", "item"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    @Column(length = 50)
    private String unit;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal rate;

    @Column(name = "gst_rate", precision = 5, scale = 2)
    private BigDecimal gstRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_invoice_id", nullable = false)
    private PurchaseInvoice purchaseInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
}