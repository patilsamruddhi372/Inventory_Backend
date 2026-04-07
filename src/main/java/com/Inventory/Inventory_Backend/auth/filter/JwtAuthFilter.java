package com.Inventory.Inventory_Backend.auth.filter;

import com.Inventory.Inventory_Backend.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        //allow preflight requests
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            filterChain.doFilter(request, response);
            return;
        }

        // Skip authentication APIs
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get Authorization  header
        String authHeader = request.getHeader("Authorization");

        // if token missing -> do not block
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // ❗ Allow request to continue (for public & preflight requests)
            filterChain.doFilter(request, response);
            return;
        }

        try{
            //extract token
            String token = authHeader.substring(7);

            // validate token
            if (!jwtUtil.isTokenValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token");
                return;
            }

            // Extract userId from token
            Long tokenUserId = jwtUtil.extractUserId(token);

            //create authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            tokenUserId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("USER"))
                    );

            //set in spring context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch(Exception e){
            //any error in token -> retun 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token Processing failed");
            return;
        }

        // Everything valid → continue
        filterChain.doFilter(request, response);
    }
}
