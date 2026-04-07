package com.Inventory.Inventory_Backend.ewaybill.repository;

import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBill;
import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EWayBillRepository extends JpaRepository<EWayBill, Long> {
    //Query for Business Isolation
    List<EWayBill> findByBusinessIdAndIsActiveTrue(Long businessId);

    //Query for Fetching Single Bill
    Optional<EWayBill> findByIdAndBusinessIdAndIsActiveTrue(Long id, Long businessId);

    //Query by Sales Invoice
    Optional<EWayBill> findBySalesInvoiceIdAndBusinessId(Long salesInvoiceId, Long businessId);

    //Query for active bills
    List<EWayBill> findByStatus(EWayBillStatus status);

    //gets the latest generated bill
    Optional<EWayBill> findTopByOrderByIdDesc();

    @Modifying
    @Query("""
           UPDATE EWayBill e
           SET e.status = 'EXPIRED'
           WHERE e.validUntil < :now
           AND e.status = 'ACTIVE'
           AND e.isActive = true
     """)
    int expiredOldBills(LocalDateTime now);

    Page<EWayBill> findByBusinessIdAndIsActiveTrue(Long businessId, Pageable pageable);

    Optional<EWayBill> findTopByBusinessIdOrderByIdDesc(Long businessId);


}
