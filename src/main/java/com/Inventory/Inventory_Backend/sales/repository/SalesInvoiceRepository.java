package com.Inventory.Inventory_Backend.sales.repository;

import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, Long> {

    /**
     * All active invoices for a business (with items eagerly loaded).
     */
    @EntityGraph(attributePaths = {"items"})
    List<SalesInvoice> findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(Long businessId);

    /**
     * Single active invoice with items.
     */
    @EntityGraph(attributePaths = {"items"})
    Optional<SalesInvoice> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);

    /**
     * Total invoice count for a business
     */
    long countByBusinessId(Long businessId);

    /**
     * Check if invoice number exists
     */
    boolean existsByBusinessIdAndInvoiceNumber(Long businessId, String invoiceNumber);

    /**
     * Latest invoice number
     */
    @Query("""
            SELECT s.invoiceNumber
            FROM SalesInvoice s
            WHERE s.businessId = :businessId
            ORDER BY s.createdAt DESC
            LIMIT 1
            """)
    Optional<String> findLatestInvoiceNumberByBusinessId(@Param("businessId") Long businessId);

    boolean existsByPartyIdAndBusinessIdAndIsDeletedFalse(Long partyId, Long businessId);
}