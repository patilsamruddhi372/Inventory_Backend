package com.Inventory.Inventory_Backend.ewaybill.dto;

import com.Inventory.Inventory_Backend.ewaybill.entity.EWayBillStatus;
import com.Inventory.Inventory_Backend.ewaybill.entity.TransportMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EWayBillResponse {
    private Long id;
    private String ewayBillNumber;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerName;
    private BigDecimal totalInvoiceValue;

    private String sellerBusinessName;

    private String buyerName;

    private String vehicleNumber;

    private TransportMode transportMode;

    private Integer distanceKm;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private EWayBillStatus status;

    private Long daysRemaining;

    private LocalDateTime createdAt;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getEwayBillNumber() {
        return ewayBillNumber;
    }

    public void setEwayBillNumber(String ewayBillNumber) {
        this.ewayBillNumber = ewayBillNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalInvoiceValue() {
        return totalInvoiceValue;
    }

    public void setTotalInvoiceValue(BigDecimal totalInvoiceValue) {
        this.totalInvoiceValue = totalInvoiceValue;
    }

    public String getSellerBusinessName() {
        return sellerBusinessName;
    }

    public void setSellerBusinessName(String sellerBusinessName) {
        this.sellerBusinessName = sellerBusinessName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    public Integer getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Integer distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public EWayBillStatus getStatus() {
        return status;
    }

    public void setStatus(EWayBillStatus status) {
        this.status = status;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
