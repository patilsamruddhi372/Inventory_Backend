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

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    // =========================================================
    // TENANT
    // =========================================================

    @Column(name = "business_id", nullable = false, updatable = false)
    private Long businessId;

    // =========================================================
    // SOFT DELETE
    // =========================================================

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // =========================================================
    // BASIC DATA
    // =========================================================

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @Column(name = "bill_number", nullable = false, length = 50)
    private String billNumber;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // =========================================================
    // FINANCIALS
    // =========================================================

    @Builder.Default
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_tax", precision = 15, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    // =========================================================
    // PAYMENT
    // =========================================================

    @Column(name = "payment_type", length = 30)
    private String paymentType;

    @Builder.Default
    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // =========================================================
    // AUDIT
    // =========================================================

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =========================================================
    // RELATIONS
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "party_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_pi_party")
    )
    private Party party;

    @Builder.Default
    @OneToMany(
            mappedBy = "purchaseInvoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<PurchaseInvoiceItem> items = new ArrayList<>();

    // =========================================================
    // HELPERS
    // =========================================================

    public void addItem(PurchaseInvoiceItem item) {

        if (item == null) return;

        items.add(item);
        item.setPurchaseInvoice(this);

        if (this.businessId != null) {
            item.setBusinessId(this.businessId);
        }
    }

    public void removeItem(PurchaseInvoiceItem item) {

        if (item == null) return;

        items.remove(item);
        item.setPurchaseInvoice(null);
    }

    public void replaceAllItems(List<PurchaseInvoiceItem> newItems) {

        clearItems();

        if (newItems != null) {
            newItems.forEach(this::addItem);
        }
    }

    public void clearItems() {

        for (PurchaseInvoiceItem item : items) {
            item.setPurchaseInvoice(null);
        }

        items.clear();
    }
}