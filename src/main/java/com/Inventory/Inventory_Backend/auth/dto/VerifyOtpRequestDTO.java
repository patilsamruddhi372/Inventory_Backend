package com.Inventory.Inventory_Backend.auth.dto;

import lombok.Data;

@Data
public class VerifyOtpRequestDTO {
    private String email;
    private String otp;
}
