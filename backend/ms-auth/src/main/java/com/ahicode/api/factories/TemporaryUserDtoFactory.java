package com.ahicode.api.factories;

import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.TemporaryUserDto;
import org.springframework.stereotype.Component;

@Component
public class TemporaryUserDtoFactory {

    public TemporaryUserDto makeTemporaryUserDto(RegistrationRequestDto requestDto, String activationCode) {
        return TemporaryUserDto.builder()
                .email(requestDto.getEmail())
                .firstname(requestDto.getFirstname())
                .lastname(requestDto.getLastname())
                .password(requestDto.getPassword())
                .activationCode(activationCode)
                .build();
    }
}
