package com.Inventory.Inventory_Backend.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String emailOrPhone;
    private String password;
}
