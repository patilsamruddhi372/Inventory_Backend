package com.Inventory.Inventory_Backend.sales.repository;

import com.Inventory.Inventory_Backend.sales.entity.SalesInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesInvoiceItemRepository
        extends JpaRepository<SalesInvoiceItem, Long> {

    // =========================================================
    // FIND ITEMS BY SALES INVOICE
    // =========================================================
    List<SalesInvoiceItem> findBySalesInvoiceId(Long salesInvoiceId);

    // =========================================================
    // DELETE ITEMS WHEN INVOICE IS DELETED
    // =========================================================
    void deleteBySalesInvoiceId(Long salesInvoiceId);

    // =========================================================
    // CHECK ITEM DEPENDENCY (USED IN ITEM DELETE)
    // =========================================================
    boolean existsByItemIdAndBusinessId(Long itemId, Long businessId);
}