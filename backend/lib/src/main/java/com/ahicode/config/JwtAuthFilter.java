package com.ahicode.config;

import com.ahicode.enums.AppRole;
import com.ahicode.exceptions.AppException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider provider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/auth/register") || path.equals("/auth/login") || path.equals("/auth/activateAccount")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extracting accessToken from headers or cookies
        String accessToken = extractAccessToken(request);

        // validate accessToken on signing
        validateAccessToken(accessToken);

        if (provider.isAccessTokenExpired(accessToken)) {
            updateAccessToken(request, response, accessToken);
        } else {
            authenticateUser(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        try {
            SecurityContextHolder.getContext().setAuthentication(provider.authenticatedAccessValidation(token));
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
            throw new AppException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private void validateAccessToken(String token) {
        if (token == null || !provider.isAccessTokenValid(token)) {
            SecurityContextHolder.clearContext();
            throw new AppException("Access token is invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        return (cookie != null) ? cookie.getValue() : null;
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            return authHeader.substring(7);
        }

        Cookie cookie = WebUtils.getCookie(request, "accessToken");

        return (cookie != null) ? cookie.getValue() : null;
    }

    private void updateAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(3600); // 1 hour
        response.addCookie(accessTokenCookie);
    }

    private void updateAccessToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        String refreshToken = getRefreshToken(request);

        if (refreshToken == null || !provider.isRefreshTokenValid(refreshToken)) {
            SecurityContextHolder.clearContext();
            throw new AppException("Refresh token is invalid", HttpStatus.UNAUTHORIZED);
        }

        if (provider.isRefreshTokenExpired(refreshToken)) {
            SecurityContextHolder.clearContext();
            throw new AppException("Refresh token is expired, please authorize again", HttpStatus.UNAUTHORIZED);
        }

        AppRole role = provider.extractRole(accessToken);
        String email = provider.extractEmail(accessToken);
        String newAccessToken = provider.generateAccessToken(email, role);

        updateAccessTokenCookie(response, newAccessToken);
        authenticateUser(newAccessToken);
    }
}
