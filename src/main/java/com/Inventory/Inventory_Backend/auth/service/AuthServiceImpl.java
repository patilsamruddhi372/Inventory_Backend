package com.Inventory.Inventory_Backend.auth.service;

import com.Inventory.Inventory_Backend.User.entity.User;
import com.Inventory.Inventory_Backend.User.repository.UserRepository;
import com.Inventory.Inventory_Backend.auth.dto.*;
import com.Inventory.Inventory_Backend.auth.entity.PasswordResetOTP;
import com.Inventory.Inventory_Backend.auth.repository.PasswordResetOTPRepository;
import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{

    private final EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetOTPRepository otpRepository;


    @Override
    public String signup(SignupRequestDTO request){

        //phone no validation
        if(!request.getPhone().matches("\\d{10}")){
            throw new RuntimeException("Invalid phone number");
        }

        //terms & condition validation
        if(!request.isAgreeToTerms()){
            throw new RuntimeException("You must agree to the terms and conditions");
        }

        //Check email exists
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        //Check phone exists
        if(userRepository.existsByPhone(request.getPhone())){
            throw new RuntimeException("Phone number already registered");
        }

        //Split full name
        String[] parts = request.getFullName().trim().split("\\s+");

        String firstName = parts[0];
        String lastName = parts.length > 1
                ? request.getFullName().substring(firstName.length()).trim()
                : "";

        //Create user
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        //save
        User savedUser = userRepository.save(user);

        //generate token
        String token = jwtUtil.generateToken(savedUser.getId());

        //return token instead of message
        return token;
    }

    @Override
    public String login(LoginRequestDTO request){
        String input = request.getEmailOrPhone();

        //find user by email or phone
        User user;

        if(input.contains("@")){
            user = userRepository.findByEmail(input)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            user = userRepository.findByPhone(input)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        //check password
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        //null check
        if (input == null || input.isEmpty()) {
            throw new RuntimeException("Email or phone is required");
        }

        //generate JWT
        String token = jwtUtil.generateToken(user.getId());

        return token;
    }

    @Override
    public String forgotPassword(ForgotPasswordRequestDTO request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        //Generate temporary password
        String tempPassword = "Temp@" + System.currentTimeMillis();

        //encode password
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        //in real app -> end email
        System.out.println("Temporary Password: " + tempPassword);

        return "Temporary password sent to your email (check console for now)";
    }

    @Transactional
    @Override
    public String sendOtp(SendOtpRequestDTO request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //generate 6 digit otp
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        //delete old otp if exists
        otpRepository.deleteByEmail(request.getEmail());

        PasswordResetOTP otpEntity = PasswordResetOTP.builder()
                .email(request.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpRepository.save(otpEntity);

        //send email
        emailService.sendOtpEmail(request.getEmail(), otp);

        return "OTP sent successfully";
    }

    @Transactional
    @Override
    public String resetPassword(ResetPasswordDTO request) {
        PasswordResetOTP otpEntity = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        //check expiry
        if(otpEntity.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP expired");
        }

        //verify otp
        if(!otpEntity.getOtp().equals(request.getOtp())){
            throw new RuntimeException("Invalid OTP");
        }

        //update Password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword((passwordEncoder.encode(request.getNewPassword())));
        userRepository.save(user);

        //mark otp as used
        otpEntity.setUsed(true);

        otpRepository.save(otpEntity);

        return "Password reset successful";
    }

    @Transactional(readOnly = true)
    @Override
    public String verifyOtp(VerifyOtpRequestDTO request) {
        PasswordResetOTP otpEntity =
                otpRepository.findTopByEmailOrderByExpiryTimeDesc(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("OTP not found"));

        System.out.println("Entered otp: " +request.getOtp());
        System.out.println("Stored otp: " + otpEntity.getOtp());

        if(Boolean.TRUE.equals(otpEntity.getUsed())) {
            throw new RuntimeException("OTP already used");
        }

        if(!otpEntity.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if(otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        return "OTP verified successfully";
    }
}
