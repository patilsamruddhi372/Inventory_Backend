// src/main/java/com/Inventory/Inventory_Backend/purchase/exception/PurchaseInvoiceNotFoundException.java

package com.Inventory.Inventory_Backend.purchase.exception;

public class PurchaseInvoiceNotFoundException extends RuntimeException {

    public PurchaseInvoiceNotFoundException(Long id, Long businessId) {
        super("Purchase invoice not found with ID: " + id
                + " for business: " + businessId);
    }

    public PurchaseInvoiceNotFoundException(String message) {
        super(message);
    }
}