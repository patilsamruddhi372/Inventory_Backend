package com.Inventory.Inventory_Backend.ewaybill.util;

public class EWayBillValidityCalculator {
    public static int calculateValidityDays(int distance) {
        return (int) Math.ceil(distance / 100.0);
    }
}
