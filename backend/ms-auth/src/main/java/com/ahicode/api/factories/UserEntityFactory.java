package com.ahicode.api.factories;

import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.enums.AppRole;
import com.ahicode.storage.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityFactory {

    public UserEntity makeUserEntity(RegistrationRequestDto request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(AppRole.USER_ROLE)
                .build();
    }
}
