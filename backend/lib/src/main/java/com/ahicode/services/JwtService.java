package com.ahicode.services;

import com.ahicode.enums.AppRole;
import org.springframework.security.core.Authentication;

import java.util.Date;

public interface JwtService {
    AppRole extractRole(String token);
    String extractEmail(String token);
    boolean isAccessTokenValid(String token);
    boolean isRefreshTokenValid(String token);
    boolean isAccessTokenExpired(String token);
    boolean isRefreshTokenExpired(String token);
    Date extractRefreshTokenExpirationTime(String token);
    String generateAccessToken(String email, AppRole role);
    String generateRefreshToken(String email, AppRole role);
    Authentication authenticatedAccessValidation(String token);
}
