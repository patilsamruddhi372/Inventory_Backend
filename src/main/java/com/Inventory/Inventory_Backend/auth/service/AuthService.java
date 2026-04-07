package com.Inventory.Inventory_Backend.auth.service;

import com.Inventory.Inventory_Backend.auth.dto.*;

public interface AuthService {
    String signup(SignupRequestDTO request);


    String login(LoginRequestDTO request);

    String forgotPassword(ForgotPasswordRequestDTO request);

    String sendOtp(SendOtpRequestDTO request);

    String resetPassword(ResetPasswordDTO request);

    String verifyOtp(VerifyOtpRequestDTO request);

}
