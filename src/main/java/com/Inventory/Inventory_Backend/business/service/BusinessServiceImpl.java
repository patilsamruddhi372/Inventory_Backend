package com.Inventory.Inventory_Backend.business.service;

import com.Inventory.Inventory_Backend.User.entity.User;
import com.Inventory.Inventory_Backend.User.repository.UserRepository;
import com.Inventory.Inventory_Backend.business.dto.BusinessRequest;
import com.Inventory.Inventory_Backend.settings.entity.Business;
import com.Inventory.Inventory_Backend.settings.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Override
    public Business createBusiness (BusinessRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Business business = new Business();

        business.setName(request.getName());
        business.setBusinessType(request.getBusinessType());
        business.setIndustry(request.getIndustry());

        business.setGstRegistered(request.getGstRegistered());
        business.setGstNumber(request.getGstNumber());

        business.setAddress(request.getAddress());
        business.setCity(request.getCity());
        business.setState(request.getState());
        business.setPincode(request.getPincode());
        business.setCountry(request.getCountry());

        business.setFinancialYear(request.getFinancialYear());
        business.setEnableStockManagement(request.getEnableStockManagement());

        business.setCreatedAt(LocalDateTime.now());
        business.setUpdatedAt(LocalDateTime.now());
        business.setIsActive(true);

        business.setUser(user);

        return businessRepository.save(business);
    }

}
