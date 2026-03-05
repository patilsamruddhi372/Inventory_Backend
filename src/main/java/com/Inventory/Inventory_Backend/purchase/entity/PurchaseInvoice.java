package com.Inventory.Inventory_Backend.purchase.entity;

import com.Inventory.Inventory_Backend.party.entity.Party;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(
        name = "purchase_invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_business_bill_number",
                        columnNames = {"business_id", "bill_number"}
                )
        }
)
@SQLDelete(sql = "UPDATE purchase_invoices SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"items", "party"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseInvoice {

    // =========================
    // PRIMARY KEY
    // =========================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    // =========================
    // TENANT FIELD
    // =========================

    @Column(name = "business_id", nullable = false, updatable = false)
    private Long businessId;

    // =========================
    // SOFT DELETE FIELD
    // =========================

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // =========================
    // BASIC INVOICE DATA
    // =========================

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @Column(name = "bill_number", nullable = false, length = 50)
    private String billNumber;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // =========================
    // FINANCIAL FIELDS
    // =========================

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "total_tax", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(name = "amount_paid", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "pending";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // =========================
    // AUDIT FIELDS
    // =========================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // =========================
    // RELATIONSHIPS
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "party_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_pi_party")
    )
    private Party party;

    /**
     * ✅ orphanRemoval = true  → clearing this list HARD-DELETES rows from DB
     * ✅ CascadeType.ALL       → persist/merge/remove cascades to children
     *
     * IMPORTANT: PurchaseInvoiceItem must NOT have @SQLDelete annotation.
     *            Items follow the parent invoice lifecycle.
     *            When invoice is soft-deleted, items are soft-deleted in service layer.
     *            When items are replaced during update, old items are HARD-DELETED
     *            by orphanRemoval so the unique constraint (purchase_invoice_id, item_id)
     *            won't be violated on re-insert.
     */
    @OneToMany(
            mappedBy = "purchaseInvoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PurchaseInvoiceItem> items = new ArrayList<>();

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Adds a single item and sets the bidirectional relationship + businessId.
     */
    public void addItem(PurchaseInvoiceItem item) {

        if (item == null) return;

        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        this.items.add(item);
        item.setPurchaseInvoice(this);

        if (this.businessId != null) {
            item.setBusinessId(this.businessId);
        }
    }

    /**
     * Removes a single item and breaks the bidirectional relationship.
     */
    public void removeItem(PurchaseInvoiceItem item) {

        if (this.items == null || item == null) return;

        this.items.remove(item);
        item.setPurchaseInvoice(null);
    }

    /**
     * ✅ SAFE way to replace all items during UPDATE.
     *
     * 1. Clears the existing managed collection → orphanRemoval HARD-DELETES old rows
     * 2. Adds each new item with proper bidirectional binding
     *
     * This avoids the duplicate key constraint violation because
     * old rows are deleted BEFORE new rows are inserted.
     */
    public void replaceAllItems(List<PurchaseInvoiceItem> newItems) {

        // ✅ Step 1: Remove all existing items using iterator (safe removal)
        if (this.items != null) {
            Iterator<PurchaseInvoiceItem> iterator = this.items.iterator();
            while (iterator.hasNext()) {
                PurchaseInvoiceItem existingItem = iterator.next();
                existingItem.setPurchaseInvoice(null);
                iterator.remove();    // orphanRemoval triggers DELETE
            }
        }

        // ✅ Step 2: Add all new items
        if (newItems != null) {
            for (PurchaseInvoiceItem newItem : newItems) {
                addItem(newItem);
            }
        }
    }

    /**
     * Clears all items. orphanRemoval will HARD-DELETE them from DB.
     */
    public void clearItems() {

        if (this.items != null) {
            Iterator<PurchaseInvoiceItem> iterator = this.items.iterator();
            while (iterator.hasNext()) {
                PurchaseInvoiceItem item = iterator.next();
                item.setPurchaseInvoice(null);
                iterator.remove();
            }
        }
    }
}