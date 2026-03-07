package com.Inventory.Inventory_Backend.sales.repository;

import com.Inventory.Inventory_Backend.sales.entity.SalesInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesInvoiceItemRepository
        extends JpaRepository<SalesInvoiceItem, Long> {

    List<SalesInvoiceItem> findBySalesInvoiceId(Long salesInvoiceId);

    void deleteBySalesInvoiceId(Long salesInvoiceId);
}