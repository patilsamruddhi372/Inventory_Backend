package com.Inventory.Inventory_Backend.ewaybill.repository;

import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBillVehicleAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EWayBillVehicleAuditRepository extends JpaRepository<EWayBillVehicleAudit, Long> {
}
