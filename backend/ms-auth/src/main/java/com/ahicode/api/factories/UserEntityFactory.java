package com.ahicode.api.factories;

import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.TemporaryUserDto;
import com.ahicode.enums.AppRole;
import com.ahicode.storage.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityFactory {

    public UserEntity makeUserEntity(TemporaryUserDto temporaryUserDto) {
        return UserEntity.builder()
                .email(temporaryUserDto.getEmail())
                .firstname(temporaryUserDto.getFirstname())
                .lastname(temporaryUserDto.getLastname())
                .role(AppRole.USER_ROLE)
                .build();
    }
}
