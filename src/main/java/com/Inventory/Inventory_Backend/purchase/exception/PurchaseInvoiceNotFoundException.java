package com.Inventory.Inventory_Backend.purchase.exception;

public class PurchaseInvoiceNotFoundException extends RuntimeException {

    public PurchaseInvoiceNotFoundException(Long id, Long businessId) {
        super(String.format(
                "Purchase invoice with ID '%d' was not found for business '%d'.",
                id, businessId
        ));
    }

    public PurchaseInvoiceNotFoundException(String message) {
        super(message);
    }

    public PurchaseInvoiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}