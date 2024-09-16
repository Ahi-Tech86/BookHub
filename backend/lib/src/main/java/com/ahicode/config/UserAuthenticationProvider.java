package com.ahicode.config;

import com.ahicode.enums.AppRole;
import com.ahicode.exceptions.AppException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAuthenticationProvider {

    @Value("${application.security.jwt.access-token.secret-key}")
    private String secretKeyToAccessToken;
    @Value("${application.security.jwt.access-token.expiration}")
    private Long accessTokenExpirationTime;
    @Value("${application.security.jwt.refresh-token.secret-key}")
    private String secretKeyToRefreshToken;

    private Key accessSignKey;
    private Key refreshSignKey;

    @PostConstruct
    private void init() {
        accessSignKey = getAccessSignKey();
        refreshSignKey = getRefreshSignKey();
    }

    public AppRole extractRole(String token) {
        Claims claims = extractAllClaims(token, accessSignKey);
        return claims.get("role", AppRole.class);
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token, accessSignKey);
        return claims.getSubject();
    }

    public boolean isAccessTokenExpired(String token) {
        return expirationValidate(token, accessSignKey);
    }

    public boolean isRefreshTokenExpired(String token) {
        return expirationValidate(token, refreshSignKey);
    }

    public boolean isAccessTokenValid(String token) {
        return validateToken(token, accessSignKey);
    }

    public boolean isRefreshTokenValid(String token) {
        return validateToken(token, refreshSignKey);
    }

    public Authentication authenticatedAccessValidation(String token) {
        return authValidateToken(token, accessSignKey);
    }

    public Authentication authenticatedRefreshValidation(String token) {
        return authValidateToken(token, refreshSignKey);
    }

    public String generateAccessToken(String email, AppRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(accessSignKey)
                .compact();
    }

    private Key getAccessSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyToAccessToken);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyToRefreshToken);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean validateToken(String token, Key signKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (RuntimeException exception) {
            throw new AppException("Token was incorrectly signed", HttpStatus.UNAUTHORIZED);
        }
    }

    private Claims extractAllClaims(String token, Key signKey) {
        return Jwts.parserBuilder()
                .setSigningKey(signKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean expirationValidate(String token, Key signKey) {
        Claims claims = extractAllClaims(token, signKey);

        Date expirationDate = claims.getExpiration();

        return expirationDate.before(new Date());
    }

    private Authentication authValidateToken(String token, Key signKey) {

        Claims claims = extractAllClaims(token, signKey);

        String email = claims.getSubject();

        return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
    }
}
