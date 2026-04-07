package com.Inventory.Inventory_Backend.common;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static Long getCurrentUserId(){
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if(principal instanceof Long){
            return (Long) principal;
        }

        throw new RuntimeException("User not authenticated");
    }
}
