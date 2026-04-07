package com.Inventory.Inventory_Backend.User.controller;

import com.Inventory.Inventory_Backend.User.dto.ChangePasswordDTO;
import com.Inventory.Inventory_Backend.User.dto.UpdateUsersDTO;
import com.Inventory.Inventory_Backend.User.dto.UserResponseDTO;
import com.Inventory.Inventory_Backend.User.service.UserService;
import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    //Get profile
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id,
                                                   HttpServletRequest request){
        //get token
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        //extract userId from token
        Long userIdFromToken = jwtUtil.extractUserId(token);

        //check access
        if(!userIdFromToken.equals(id)){
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //Update Profile
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUsersDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    //Change password
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(id, dto);
        return ResponseEntity.ok("Password Updated successfully");
    }
}
