package com.Inventory.Inventory_Backend.settings.entity;

import com.Inventory.Inventory_Backend.User.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String businessType;
    private String industry;

    private Boolean gstRegistered;
    private String gstNumber;

    private String address;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private String financialYear;
    private String currency;
    private Boolean enableStockManagement;

    private String logoUrl;

    private String subscriptionPlan;
    private LocalDate subscriptionStart;
    private LocalDate subscriptionEnd;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "billing_name")
    private String billingName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "billing_city")
    private String billingCity;

    @Column(name = "billing_state")
    private String billingState;

    @Column(name = "billing_zipcode")
    private String billingZipcode;

    @Column(name = "billing_country")
    private String billingCountry;

    @Column(name = "payment_method")
    private String paymentMethod;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
