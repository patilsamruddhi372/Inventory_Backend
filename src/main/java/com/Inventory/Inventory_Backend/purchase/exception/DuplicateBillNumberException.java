package com.Inventory.Inventory_Backend.purchase.exception;

public class DuplicateBillNumberException extends RuntimeException {

    public DuplicateBillNumberException(String billNumber) {
        super(String.format(
                "Purchase invoice with bill number '%s' already exists for this business.",
                billNumber
        ));
    }

    public DuplicateBillNumberException(String billNumber, Throwable cause) {
        super(String.format(
                "Purchase invoice with bill number '%s' already exists for this business.",
                billNumber
        ), cause);
    }
}