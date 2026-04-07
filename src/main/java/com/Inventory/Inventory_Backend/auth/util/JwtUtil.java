package com.Inventory.Inventory_Backend.auth.util;


import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET = "mysecretkeymysecretkeymysecretkey"; // 32+ chars
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    //generate token
    public String generateToken(Long userId){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 )) // 1 hours
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //extract userId
    public Long extractUserId(String token){
        return Long.parseLong(getClaims(token).getSubject());
    }

    //validate token
    public boolean isTokenValid(String token){
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserIdFromRequest(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return extractUserId(token);
    }
}
