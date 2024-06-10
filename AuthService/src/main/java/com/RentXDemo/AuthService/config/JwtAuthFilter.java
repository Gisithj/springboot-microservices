package com.RentXDemo.AuthService.config;

import com.RentXDemo.AuthService.service.JwtService;
import com.RentXDemo.AuthService.service.impl.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component @AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    UserDetailServiceImpl userDetailServiceImpl;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);
        System.out.println(token);
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token is missing");
        }else{
            try {
                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    authenticateUser(username, token, request);
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token has expired");
                return;
            }

            filterChain.doFilter(request, response);

        }


    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludeUrlPatterns = Arrays.asList(
                "/auth/login",
                "/auth/sign-up",
                "/auth/refreshToken",
                "/swagger-ui/",
                "/v3/api-docs",
                "/swagger-ui.html");
        String path = request.getRequestURI();
        System.out.println(path);
        System.out.println(excludeUrlPatterns.stream().anyMatch(path::startsWith));
        return excludeUrlPatterns.stream().anyMatch(path::startsWith);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void authenticateUser(String username, String token, HttpServletRequest request) {
        UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);
        if (jwtService.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
}
