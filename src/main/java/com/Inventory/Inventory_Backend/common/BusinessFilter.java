package com.Inventory.Inventory_Backend.common;

import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import com.Inventory.Inventory_Backend.settings.repository.BusinessRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BusinessFilter extends OncePerRequestFilter {

    private final BusinessContext businessContext;
    private final BusinessRepository businessRepository;
    private final JwtUtil jwtUtil;

    public BusinessFilter(BusinessContext businessContext,
                          BusinessRepository businessRepository,
                          JwtUtil jwtUtil) {
        this.businessContext = businessContext;
        this.businessRepository = businessRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ 1. Skip auth APIs
        if (path.startsWith("/api/auth") || path.startsWith("/api/business")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. Skip preflight requests (VERY IMPORTANT)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String businessIdHeader = request.getHeader("X-Business-Id");
        String authHeader = request.getHeader("Authorization");

        // ✅ 3. If headers missing → just continue (DON’T CRASH)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        //if businessid not present -> allow but dont set context
        if(businessIdHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long businessId = Long.parseLong(businessIdHeader);

            boolean exists = businessRepository.existsByIdAndUserId(businessId, userId);

            if (!exists) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access denied for this business");
                return;
            }

            businessContext.setBusinessId(businessId);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid request");
            return;
        }

        filterChain.doFilter(request, response);
    }
}