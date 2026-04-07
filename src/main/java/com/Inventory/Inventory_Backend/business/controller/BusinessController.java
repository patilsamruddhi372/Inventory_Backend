package com.Inventory.Inventory_Backend.business.controller;

import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import com.Inventory.Inventory_Backend.business.dto.BusinessRequest;
import com.Inventory.Inventory_Backend.business.service.BusinessService;
import com.Inventory.Inventory_Backend.common.AuthUtil;
import com.Inventory.Inventory_Backend.settings.entity.Business;
import com.Inventory.Inventory_Backend.settings.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BusinessController {

    private final BusinessService businessService;
    private final BusinessRepository businessRepository;
    private final JwtUtil jwtUtil;


    @PostMapping
    public Business createBusiness(@RequestBody BusinessRequest request,
                                   Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return businessService.createBusiness(request, userId);
    }

    @GetMapping("/my")
    public List<Business> getMyBusinesses(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        return businessRepository.findAllByUserId(userId);
    }


}
