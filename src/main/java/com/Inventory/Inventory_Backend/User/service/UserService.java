package com.Inventory.Inventory_Backend.User.service;

import com.Inventory.Inventory_Backend.User.dto.ChangePasswordDTO;
import com.Inventory.Inventory_Backend.User.dto.UpdateUsersDTO;
import com.Inventory.Inventory_Backend.User.dto.UserResponseDTO;
import com.Inventory.Inventory_Backend.User.entity.User;

public interface UserService {
    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UpdateUsersDTO dto);

    void changePassword(Long id, ChangePasswordDTO dto);
}
