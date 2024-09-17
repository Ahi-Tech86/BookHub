package com.ahicode.api.services.interfaces;

import com.ahicode.storage.entities.UserEntity;

public interface TokenService {

    void createAndSaveToken(UserEntity user);
    String getTokenByUserEmail(String email);
}
