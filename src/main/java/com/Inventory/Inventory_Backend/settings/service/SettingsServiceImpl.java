package com.Inventory.Inventory_Backend.settings.service;

import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import com.Inventory.Inventory_Backend.settings.dto.BillingDetailsRequestDTO;
import com.Inventory.Inventory_Backend.settings.dto.SettingsResponseDTO;
import com.Inventory.Inventory_Backend.settings.dto.SubscriptionRequestDTO;
import com.Inventory.Inventory_Backend.settings.dto.UpdateSettingsRequestDTO;
import com.Inventory.Inventory_Backend.settings.entity.Business;
import com.Inventory.Inventory_Backend.settings.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService{

    private final BusinessRepository businessRepository;

    private final JwtUtil jwtUtil;

    @Override
    public SettingsResponseDTO getSettings(Long businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        SettingsResponseDTO response = new SettingsResponseDTO();

        response.setBusinessName(business.getName());
        response.setGstin(business.getGstNumber());
        response.setAddress(business.getAddress());
        response.setCity(business.getCity());
        response.setState(business.getState());
        response.setPincode(business.getPincode());

        response.setGstEnabled(business.getGstRegistered());
        response.setStockEnabled(business.getEnableStockManagement());

        // Subscription from same table
        response.setPlan(business.getSubscriptionPlan());
        response.setValidFrom(business.getSubscriptionStart());
        response.setValidTill(business.getSubscriptionEnd());

        if (business.getSubscriptionEnd() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(),
                    business.getSubscriptionEnd()
            );
            response.setDaysRemaining(days);
        }

        return response;
    }

    @Override
    public void updateSettings(Long businessId, UpdateSettingsRequestDTO request) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setName(request.getBusinessName());
        business.setGstNumber(request.getGstin());
        business.setAddress(request.getAddress());
        business.setCity(request.getCity());
        business.setState(request.getState());
        business.setPincode(request.getPincode());

        business.setGstRegistered(request.getGstEnabled());
        business.setEnableStockManagement(request.getStockEnabled());

        businessRepository.save(business);
    }

    @Override
    public void createSubscription(SubscriptionRequestDTO request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        String plan = request.getPlan();

        // ✅ FREE PLAN LOGIC
        if ("FREE".equalsIgnoreCase(plan)) {

            business.setSubscriptionPlan("FREE");

            // ❗ VERY IMPORTANT: clear old paid data
            business.setSubscriptionStart(null);
            business.setSubscriptionEnd(null);

            // (Optional) Clear billing if you want clean state
            business.setBillingName(null);
            business.setPhone(null);
            business.setBillingAddress(null);
            business.setBillingCity(null);
            business.setBillingState(null);
            business.setBillingZipcode(null);
            business.setBillingCountry(null);
            business.setPaymentMethod(null);

            businessRepository.save(business);
            return; // 🚨 STOP here for FREE plan
        }

        // ✅ PAID PLAN LOGIC

        business.setSubscriptionPlan(plan);
        business.setSubscriptionStart(LocalDate.now());

        String cycle = request.getBillingCycle() != null
                ? request.getBillingCycle().trim()
                : "";

        if ("YEARLY".equalsIgnoreCase(cycle)) {
            business.setSubscriptionEnd(LocalDate.now().plusYears(1));
        } else {
            business.setSubscriptionEnd(LocalDate.now().plusMonths(1));
        }

        // ✅ Billing Details
        business.setBillingName(request.getBillingName());
        business.setPhone(request.getPhone());
        business.setBillingAddress(request.getStreetAddress());
        business.setBillingCity(request.getCity());
        business.setBillingState(request.getState());
        business.setBillingZipcode(request.getZip());
        business.setBillingCountry(request.getCountry());
        business.setPaymentMethod(request.getPaymentType());

        // ✅ GST
        business.setGstRegistered(request.getGstRegistered());
        business.setGstNumber(request.getGstin());

        businessRepository.save(business);
    }

    @Override
    public void saveBillingDetails(BillingDetailsRequestDTO request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setBillingName(request.getBillingName());
        business.setPhone(request.getPhone());
        business.setBillingAddress(request.getBillingAddress());
        business.setBillingCity(request.getBillingCity());
        business.setBillingState(request.getBillingState());
        business.setBillingZipcode(request.getBillingZipcode());
        business.setBillingCountry(request.getBillingCountry());
        business.setPaymentMethod(request.getPaymentMethod());

        businessRepository.save(business);
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }

    @Override
    public BillingDetailsRequestDTO getBillingDetails(Long businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        BillingDetailsRequestDTO response = new BillingDetailsRequestDTO();

        response.setBusinessId(businessId);
        response.setBillingName(business.getBillingName());
        response.setPhone(business.getPhone());
        response.setBillingAddress(business.getBillingAddress());
        response.setBillingCity(business.getBillingCity());
        response.setBillingState(business.getBillingState());
        response.setBillingZipcode(business.getBillingZipcode());
        response.setBillingCountry(business.getBillingCountry());
        response.setPaymentMethod(business.getPaymentMethod());

        return response;
    }

    @Override
    public SettingsResponseDTO getSettingsByUserId(Long userId) {

        List<Business> businesses = businessRepository.findAllByUserId(userId);

        if (businesses == null || businesses.isEmpty()) {
            throw new RuntimeException("No business found for user");
        }

        // 🔥 pick FIRST business (for now)
        Business business = businesses.get(0);

        SettingsResponseDTO response = new SettingsResponseDTO();

        response.setId(business.getId()); // IMPORTANT
        response.setBusinessName(business.getName());
        response.setGstin(business.getGstNumber());
        response.setAddress(business.getAddress());
        response.setCity(business.getCity());
        response.setState(business.getState());
        response.setPincode(business.getPincode());

        response.setGstEnabled(business.getGstRegistered());
        response.setStockEnabled(business.getEnableStockManagement());

        return response;
    }

}
