package com.Inventory.Inventory_Backend.quotation.repository;

import com.Inventory.Inventory_Backend.quotation.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, Long> {

    // ======================================================
    // FIND ITEMS BY QUOTATION
    // ======================================================

    List<QuotationItem> findByQuotationIdAndBusinessId(Long quotationId, Long businessId);


    // ======================================================
    // CHECK ITEM DEPENDENCY
    // ======================================================

    boolean existsByItemIdAndBusinessId(Long itemId, Long businessId);

}