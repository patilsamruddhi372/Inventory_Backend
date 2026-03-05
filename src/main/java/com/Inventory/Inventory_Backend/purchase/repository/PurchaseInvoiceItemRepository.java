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

    List<PurchaseInvoiceItem> findByPurchaseInvoiceId(Long purchaseInvoiceId);

    // ✅ REMOVED deleteByPurchaseInvoiceId()
    //    orphanRemoval on PurchaseInvoice.items handles deletion now
    //    No more manual bulk delete needed

    // ✅ Updated: Only count items whose PARENT invoice is NOT soft-deleted
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
}