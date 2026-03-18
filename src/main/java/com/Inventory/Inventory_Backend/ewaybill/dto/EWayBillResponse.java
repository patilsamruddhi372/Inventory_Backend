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

    //seller details
    private String sellerBusinessName;
    private String sellerGstin;
    public String sellerState;

    //buyer details
    private String buyerName;
    private String buyerGstin;
    private String buyerState;

    //transport details
    private String transporterId;
    private String transporterName;
    private String transporterDocumentNo;
    private LocalDate transporterDocumentDate;

    private String vehicleNumber;
    private TransportMode transportMode;
    private Integer distanceKm;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private EWayBillStatus status;
    private Long daysRemaining;
    private LocalDateTime createdAt;

    //getters and setters
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

    public String getSellerGstin() {
        return sellerGstin;
    }

    public void setSellerGstin(String sellerGstin) {
        this.sellerGstin = sellerGstin;
    }

    public String getSellerState() {
        return sellerState;
    }

    public void setSellerState(String sellerState) {
        this.sellerState = sellerState;
    }

    public String getBuyerGstin() {
        return buyerGstin;
    }

    public void setBuyerGstin(String buyerGstin) {
        this.buyerGstin = buyerGstin;
    }

    public String getBuyerState() {
        return buyerState;
    }

    public void setBuyerState(String buyerState) {
        this.buyerState = buyerState;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public String getTransporterDocumentNo() {
        return transporterDocumentNo;
    }

    public void setTransporterDocumentNo(String transporterDocumentNo) {
        this.transporterDocumentNo = transporterDocumentNo;
    }

    public LocalDate getTransporterDocumentDate() {
        return transporterDocumentDate;
    }

    public void setTransporterDocumentDate(LocalDate transporterDocumentDate) {
        this.transporterDocumentDate = transporterDocumentDate;
    }
}
