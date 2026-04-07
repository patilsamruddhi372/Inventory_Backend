package com.Inventory.Inventory_Backend.purchase.repository;

import com.Inventory.Inventory_Backend.purchase.entity.PurchaseInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PurchaseInvoiceItemRepository extends JpaRepository<PurchaseInvoiceItem, Long> {

    // ==========================================================
    // FIND ITEMS BY INVOICE
    // ==========================================================
    List<PurchaseInvoiceItem> findByPurchaseInvoiceId(Long purchaseInvoiceId);

    // ==========================================================
    // TOTAL PURCHASED QUANTITY FOR ITEM
    // (Used for stock calculation / analytics)
    // ==========================================================
    @Query("""
            SELECT COALESCE(SUM(pii.quantity), 0)
            FROM PurchaseInvoiceItem pii
            JOIN pii.purchaseInvoice pi
            WHERE pii.businessId = :businessId
              AND pii.itemId = :itemId
              AND pi.isDeleted = false
           """)
    BigDecimal getTotalQuantityPurchasedForItem(
            @Param("businessId") Long businessId,
            @Param("itemId") Long itemId
    );

    // ==========================================================
    // FIND ALL PURCHASE RECORDS FOR AN ITEM
    // ==========================================================
    List<PurchaseInvoiceItem> findByBusinessIdAndItemId(
            Long businessId,
            Long itemId
    );

    // ==========================================================
    // CHECK ITEM DEPENDENCY (USED IN ITEM DELETE)
    // ==========================================================
    boolean existsByItemIdAndBusinessId(Long itemId, Long businessId);


}