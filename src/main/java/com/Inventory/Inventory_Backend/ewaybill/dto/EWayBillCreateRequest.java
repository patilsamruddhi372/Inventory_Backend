package com.Inventory.Inventory_Backend.ewaybill.dto;

import com.Inventory.Inventory_Backend.ewaybill.entity.TransportMode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EWayBillCreateRequest {

    @NotNull
    private Long salesInvoiceId;
    private String transporterId;
    private String transporterName;
    private String transportDocumentNumber;
    private LocalDate transportDocumentDate;
    @NotNull
    private TransportMode transportMode;
    @NotBlank
    private String vehicleNumber;
    @Min(1)
    private Integer distanceKm;

    public Long getSalesInvoiceId() {
        return salesInvoiceId;
    }

    public void setSalesInvoiceId(Long salesInvoiceId) {
        this.salesInvoiceId = salesInvoiceId;
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

    public String getTransportDocumentNumber() {
        return transportDocumentNumber;
    }

    public void setTransportDocumentNumber(String transportDocumentNumber) {
        this.transportDocumentNumber = transportDocumentNumber;
    }

    public LocalDate getTransportDocumentDate() {
        return transportDocumentDate;
    }

    public void setTransportDocumentDate(LocalDate transportDocumentDate) {
        this.transportDocumentDate = transportDocumentDate;
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
}
