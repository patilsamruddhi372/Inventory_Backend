// src/main/java/com/Inventory/Inventory_Backend/purchase/exception/DuplicateBillNumberException.java

package com.Inventory.Inventory_Backend.purchase.exception;

public class DuplicateBillNumberException extends RuntimeException {

    public DuplicateBillNumberException(String billNumber) {
        super("Bill number '" + billNumber
                + "' already exists for this business");
    }
}