package com.Inventory.Inventory_Backend.auth.repository;

import com.Inventory.Inventory_Backend.auth.entity.PasswordResetOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByEmail(String email);

    void deleteByEmail(String email);

    Optional<PasswordResetOTP> findTopByEmailOrderByExpiryTimeDesc(String email);
}
