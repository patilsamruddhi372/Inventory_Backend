package com.Inventory.Inventory_Backend.ewaybill.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eway_bill_vehicle_audit")
public class EWayBillVehicleAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ewayBillId;

    private Long businessId;

    private String oldVehicleNumber;

    private String newVehicleNumber;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEwayBillId() {
        return ewayBillId;
    }

    public void setEwayBillId(Long ewayBillId) {
        this.ewayBillId = ewayBillId;
    }

    public String getOldVehicleNumber() {
        return oldVehicleNumber;
    }

    public void setOldVehicleNumber(String oldVehicleNumber) {
        this.oldVehicleNumber = oldVehicleNumber;
    }

    public String getNewVehicleNumber() {
        return newVehicleNumber;
    }

    public void setNewVehicleNumber(String newVehicleNumber) {
        this.newVehicleNumber = newVehicleNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
