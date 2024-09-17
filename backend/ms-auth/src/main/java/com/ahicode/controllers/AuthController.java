package com.ahicode.controllers;

import com.ahicode.api.dtos.AccountActivationRequestDto;
import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.services.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public String login() {
        return "";
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegistrationRequestDto requestDto) {
        return authService.registerUser(requestDto);
    }

    @PostMapping("/activateAccount")
    public String activateAccount(@RequestBody AccountActivationRequestDto requestDto) {
        return authService.activateAccount(requestDto);
    }
}
