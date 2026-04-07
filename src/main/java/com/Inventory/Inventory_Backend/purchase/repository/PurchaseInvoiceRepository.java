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

    // ==========================================================
    // FIND ALL PURCHASES
    // ==========================================================
    List<PurchaseInvoice> findByBusinessIdAndIsDeletedFalseOrderByCreatedAtDesc(Long businessId);

    // ==========================================================
    // FIND BY ID
    // ==========================================================
    Optional<PurchaseInvoice> findByIdAndBusinessIdAndIsDeletedFalse(
            Long id,
            Long businessId
    );

    // ==========================================================
    // BILL NUMBER VALIDATION
    // ==========================================================
    boolean existsByBusinessIdAndBillNumberAndIsDeletedFalse(
            Long businessId,
            String billNumber
    );

    boolean existsByBusinessIdAndBillNumberAndIdNotAndIsDeletedFalse(
            Long businessId,
            String billNumber,
            Long id
    );

    // ==========================================================
    // FILTER BY STATUS
    // ==========================================================
    List<PurchaseInvoice> findByBusinessIdAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            Long businessId,
            String status
    );

    // ==========================================================
    // FILTER BY PARTY
    // ==========================================================
    List<PurchaseInvoice> findByBusinessIdAndPartyIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long businessId,
            Long partyId
    );

    // ==========================================================
    // FILTER BY PAYMENT TYPE (NEW - OPTIONAL)
    // ==========================================================
    List<PurchaseInvoice> findByBusinessIdAndPaymentTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            Long businessId,
            String paymentType
    );

    // ==========================================================
    // FETCH INVOICE WITH ITEMS + PARTY
    // ==========================================================
    @Query("""
            SELECT DISTINCT pi
            FROM PurchaseInvoice pi
            LEFT JOIN FETCH pi.items pii
            LEFT JOIN FETCH pi.party
            LEFT JOIN FETCH pii.item
            WHERE pi.id = :id
            AND pi.businessId = :businessId
            AND pi.isDeleted = false
           """)
    Optional<PurchaseInvoice> findByIdWithItemsAndParty(
            @Param("id") Long id,
            @Param("businessId") Long businessId
    );


    boolean existsByPartyIdAndBusinessIdAndIsDeletedFalse(
            Long partyId,
            Long businessId);
}