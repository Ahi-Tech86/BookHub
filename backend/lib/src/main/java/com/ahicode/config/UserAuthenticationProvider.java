package com.ahicode.config;

import com.ahicode.enums.AppRole;
import com.ahicode.services.JwtServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider {

    private final JwtServiceImpl jwtService;

    public boolean isAccessTokenExpired(String token) {
        return jwtService.isAccessTokenExpired(token);
    }

    public boolean isRefreshTokenExpired(String token) {
        return jwtService.isRefreshTokenExpired(token);
    }

    public boolean isAccessTokenValid(String token) {
        return jwtService.isAccessTokenValid(token);
    }

    public boolean isRefreshTokenValid(String token) {
        return jwtService.isRefreshTokenValid(token);
    }

    public AppRole extractRole(String token) {
        return jwtService.extractRole(token);
    }

    public String extractEmail(String token) {
        return jwtService.extractEmail(token);
    }

    public String generateAccessToken(String email, AppRole role) {
        return jwtService.generateAccessToken(email, role);
    }

    public Authentication authenticatedAccessValidation(String token) {
        return jwtService.authenticatedAccessValidation(token);
    }
}
