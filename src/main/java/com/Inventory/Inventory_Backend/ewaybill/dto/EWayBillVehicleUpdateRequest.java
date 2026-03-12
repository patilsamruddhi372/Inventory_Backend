package com.Inventory.Inventory_Backend.ewaybill.dto;

import com.Inventory.Inventory_Backend.ewaybill.entity.TransportMode;

public class EWayBillVehicleUpdateRequest {
    public String vehicleNumber;
    private TransportMode transportMode;

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
}
