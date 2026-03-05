package com.Inventory.Inventory_Backend.purchase.repository;

import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long> {

    List<PurchaseInvoice> findByBusinessIdOrderByCreatedAtDesc(Long businessId);

    Optional<PurchaseInvoice> findByIdAndBusinessId(Long id, Long businessId);

    boolean existsByBusinessIdAndBillNumber(Long businessId, String billNumber);

    // ⭐ useful for update validation
    boolean existsByBusinessIdAndBillNumberAndIdNot(
            Long businessId,
            String billNumber,
            Long id
    );

    List<PurchaseInvoice> findByBusinessIdAndStatusOrderByCreatedAtDesc(
            Long businessId, String status
    );

    List<PurchaseInvoice> findByBusinessIdAndPartyIdOrderByCreatedAtDesc(
            Long businessId, Long partyId
    );

    // ✅ Since @Where is removed from PurchaseInvoiceItem,
    //    no filter conflict anymore. This query works cleanly.
    //    Items are HARD-DELETED by orphanRemoval, so only
    //    real active items exist in the table.
    @Query("""
            SELECT DISTINCT pi FROM PurchaseInvoice pi
            LEFT JOIN FETCH pi.items pii
            LEFT JOIN FETCH pi.party
            LEFT JOIN FETCH pii.item
            WHERE pi.id = :id
            AND pi.businessId = :businessId
            """)
    Optional<PurchaseInvoice> findByIdWithItemsAndParty(
            @Param("id") Long id,
            @Param("businessId") Long businessId
    );
}