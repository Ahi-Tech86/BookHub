package com.ahicode.api.services;

import com.ahicode.api.services.interfaces.TokenService;
import com.ahicode.storage.entities.UserEntity;
import com.ahicode.storage.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final SymmetricEncryptionServiceImpl encryptionService;

    @Override
    public void createAndSaveToken(UserEntity user) {

    }

    @Override
    public String getTokenByUserEmail(String email) {
        return "";
    }
}
