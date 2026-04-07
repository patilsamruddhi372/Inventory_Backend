package com.Inventory.Inventory_Backend.auth.controller;

import com.Inventory.Inventory_Backend.User.entity.User;
import com.Inventory.Inventory_Backend.auth.dto.*;
import com.Inventory.Inventory_Backend.auth.service.AuthService;
import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    //signup
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO request){
        return ResponseEntity.ok(authService.signup(request));
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request){
        return ResponseEntity.ok(authService.login(request));
    }

    //forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request){
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    //send otp
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequestDTO request){
        return ResponseEntity.ok(authService.sendOtp(request));
    }

    //password reset
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO request){
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    //otp verification
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

}
