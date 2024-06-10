package com.RentXDemo.AuthService.service;


import com.RentXDemo.AuthService.entity.Role;
import com.RentXDemo.AuthService.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;


@Component
public class JwtService {
    @Autowired
    UserRepository userRepository;
    public static final String SECRET = "THISISMYSECRETKEYFORJWTGENERATIONANDVALIDATION";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("In the extractClaim");
        System.out.println(claims);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()));
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return false;
        }
    }
    public String GenerateToken(String username){
        Set<Role> roles = userRepository.findByUsername(username).getRoles();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, username);
    }
    private String createToken(Map<String, Object> claims, String username) {
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
        return token;
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        System.out.println(Keys.hmacShaKeyFor(keyBytes));
        return Keys.hmacShaKeyFor(keyBytes);
    }

}