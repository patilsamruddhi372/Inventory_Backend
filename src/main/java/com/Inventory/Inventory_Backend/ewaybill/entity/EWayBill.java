package com.Inventory.Inventory_Backend.ewaybill.entity;

import com.Inventory.Inventory_Backend.sales.entity.SalesInvoice;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eway_bills")
public class EWayBill {
    public static EWayBillStatus CANCELLED;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long businessId;

    @Column(name = "sales_invoice_id")
    private Long salesInvoiceId;

    @ManyToOne
    @JoinColumn(name = "sales_invoice_id", insertable=false, updatable=false)
    private SalesInvoice salesInvoice;
    private String ewayBillNumber;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalInvoiceValue;
    private String sellerGstin;
    private String sellerBusinessName;
    private String sellerState;
    private String buyerGstin;
    private String buyerBusinessName;
    private String buyerState;
    private String transporterId;
    private String transporterName;
    private String transporterDocumentNo;
    private LocalDate transporterDocumentDate;

    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;

    private String vehicleNumber;

    private Integer distanceKm;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    @Enumerated(EnumType.STRING)
    private EWayBillStatus status;

    private Boolean isActive = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getSalesInvoiceId() {
        return salesInvoiceId;
    }

    public void setSalesInvoiceId(Long salesInvoiceId) {
        this.salesInvoiceId = salesInvoiceId;
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

    public BigDecimal getTotalInvoiceValue() {
        return totalInvoiceValue;
    }

    public void setTotalInvoiceValue(BigDecimal totalInvoiceValue) {
        this.totalInvoiceValue = totalInvoiceValue;
    }

    public String getSellerGstin() {
        return sellerGstin;
    }

    public void setSellerGstin(String sellerGstin) {
        this.sellerGstin = sellerGstin;
    }

    public String getSellerBusinessName() {
        return sellerBusinessName;
    }

    public void setSellerBusinessName(String sellerBusinessName) {
        this.sellerBusinessName = sellerBusinessName;
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

    public String getBuyerBusinessName() {
        return buyerBusinessName;
    }

    public void setBuyerBusinessName(String buyerBusinessName) {
        this.buyerBusinessName = buyerBusinessName;
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

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSalesInvoice(SalesInvoice invoice) {
        this.salesInvoice = invoice;
    }

    public void setTransportMode(com.Inventory.Inventory_Backend.sales.dto.TransportMode transportMode) {
    }

    public void setTransportDocumentDate(LocalDate transportDocumentDate) {
    }

    public void setTransportDocumentNumber(String transportDocumentNumber) {
    }
}
