package com.ahicode.services;

import com.ahicode.enums.AppRole;
import org.springframework.security.core.Authentication;

public interface JwtService {
    AppRole extractRole(String token);
    String extractEmail(String token);
    boolean isAccessTokenValid(String token);
    boolean isRefreshTokenValid(String token);
    boolean isAccessTokenExpired(String token);
    boolean isRefreshTokenExpired(String token);
    String generateAccessToken(String email, AppRole role);
    Authentication authenticatedAccessValidation(String token);
    Authentication authenticatedRefreshValidation(String token);
}
