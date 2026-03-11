package com.Inventory.Inventory_Backend.sales.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "sales_invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_invoice_number_per_business",
                        columnNames = {"business_id", "invoice_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "items")
@EqualsAndHashCode(of = "id")
public class SalesInvoice {

    // ==========================================================
    // PRIMARY KEY
    // ==========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================================
    // BUSINESS / PARTY
    // ==========================================================

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    // ==========================================================
    // INVOICE INFO
    // ==========================================================

    @Column(name = "invoice_number", nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // ==========================================================
    // FINANCIAL
    // ==========================================================

    @Builder.Default
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_discount", precision = 15, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_cgst", precision = 15, scale = 2)
    private BigDecimal totalCgst = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_sgst", precision = 15, scale = 2)
    private BigDecimal totalSgst = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_igst", precision = 15, scale = 2)
    private BigDecimal totalIgst = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_tax", precision = 15, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    // ==========================================================
    // PAYMENT
    // ==========================================================

    @Column(name = "payment_type", length = 30)
    private String paymentType;

    @Builder.Default
    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "amount_paid", precision = 15, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // ==========================================================
    // STATUS
    // ==========================================================

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "pending";

    // ==========================================================
    // SYSTEM
    // ==========================================================

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==========================================================
    // QUOTATION LINK (IMPORTANT FOR CONVERSION)
    // ==========================================================

    @Column(name = "quotation_id")
    private Long quotationId;

    // ==========================================================
    // RELATIONSHIP
    // ==========================================================

    @Builder.Default
    @OneToMany(
            mappedBy = "salesInvoice",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SalesInvoiceItem> items = new ArrayList<>();

    // ==========================================================
    // LIFECYCLE
    // ==========================================================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==========================================================
    // HELPERS
    // ==========================================================

    public void addItem(SalesInvoiceItem item) {
        if (item == null) return;

        items.add(item);
        item.setSalesInvoice(this);
    }

    public void removeItem(SalesInvoiceItem item) {
        if (item == null) return;

        items.remove(item);
        item.setSalesInvoice(null);
    }

    public void clearItems() {
        for (SalesInvoiceItem item : items) {
            item.setSalesInvoice(null);
        }
        items.clear();
    }
}