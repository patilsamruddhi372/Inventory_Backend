package com.Inventory.Inventory_Backend.quotation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "quotations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"business_id", "quotation_number"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "quotation_number", nullable = false)
    private String quotationNumber;

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @Column(name = "quotation_date")
    private LocalDate quotationDate;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    private BigDecimal subtotal;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "shipping_charges")
    private BigDecimal shippingCharges;

    @Column(name = "round_off")
    private BigDecimal roundOff;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    private String currency;

    /**
     * DRAFT
     * SENT
     * APPROVED
     * REJECTED
     * CONVERTED
     */
    private String status;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_time")
    private String deliveryTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    // ============================================================
    // CONVERSION DETAILS
    // ============================================================

    @Column(name = "converted_to_invoice_id")
    private Long convertedToInvoiceId;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    // ============================================================
    // SOFT DELETE
    // ============================================================

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // ============================================================
    // AUDIT
    // ============================================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ============================================================
    // RELATIONSHIP
    // ============================================================

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", insertable = false, updatable = false)
    private List<QuotationItem> items;

    // ============================================================
    // AUTO TIMESTAMP
    // ============================================================

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = "DRAFT";
        }

        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }
}