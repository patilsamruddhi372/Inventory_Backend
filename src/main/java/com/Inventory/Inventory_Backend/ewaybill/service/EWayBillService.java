package com.Inventory.Inventory_Backend.ewaybill.service;

import com.Inventory.Inventory_Backend.ewaybill.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EWayBillService {

    EWayBillResponse createEWayBill(Long businessId, EWayBillCreateRequest request);

    EWayBillResponse getEWayBill(Long businessId, Long id);

    List<EWayBillResponse> getAllEWayBills(Long businessId);

    EWayBillResponse updateEWayBill(Long businessId, Long id, EWayBillUpdateRequest request);

    void deleteEWayBill(Long businessId, Long id);

    EWayBillResponse updateVehicle(Long businessId, Long id, EWayBillVehicleUpdateRequest request);

    EWayBillResponse extendValidity(Long businessId, Long id, EWayBillExtendValidityRequest request);

    void cancelEWayBill(Long businessId, Long id);

    Page<EWayBillResponse> getAllBills(Long businessId, int page, int size);

}
