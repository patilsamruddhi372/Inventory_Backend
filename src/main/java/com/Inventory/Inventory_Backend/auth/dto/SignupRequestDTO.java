package com.Inventory.Inventory_Backend.auth.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private boolean agreeToTerms;
}
