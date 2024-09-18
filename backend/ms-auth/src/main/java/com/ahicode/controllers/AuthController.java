package com.ahicode.controllers;

import com.ahicode.api.dtos.AccountActivationRequestDto;
import com.ahicode.api.dtos.CredentialRequestDto;
import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.UserDto;
import com.ahicode.api.services.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public UserDto login(HttpServletResponse response, @RequestBody CredentialRequestDto requestDto) {
        List<Object> authenticatedUser = authService.login(requestDto);

        UserDto userDto = (UserDto) authenticatedUser.get(0);
        String accessToken = (String) authenticatedUser.get(1);
        String refreshToken = (String) authenticatedUser.get(2);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(24 * 60 * 60);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return userDto;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegistrationRequestDto requestDto) {
        return authService.registerUser(requestDto);
    }

    @PostMapping("/activateAccount")
    public UserDto activateAccount(@RequestBody AccountActivationRequestDto requestDto) {
        return authService.activateAccount(requestDto);
    }
}
