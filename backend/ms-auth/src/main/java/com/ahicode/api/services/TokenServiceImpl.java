package com.ahicode.api.services;

import com.ahicode.api.factories.RefreshTokenEntityFactory;
import com.ahicode.api.services.interfaces.TokenService;
import com.ahicode.enums.AppRole;
import com.ahicode.exceptions.AppException;
import com.ahicode.services.JwtServiceImpl;
import com.ahicode.storage.entities.RefreshTokenEntity;
import com.ahicode.storage.entities.UserEntity;
import com.ahicode.storage.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtServiceImpl jwtService;
    private final TokenRepository repository;
    private final RefreshTokenEntityFactory factory;
    private final SymmetricEncryptionServiceImpl encryptionService;

    @Override
    @Transactional
    public void createAndSaveToken(UserEntity user) {
        String email = user.getEmail();
        AppRole role = user.getRole();

        // generating refresh token and extract expiration time
        String refreshToken = jwtService.generateRefreshToken(email, role);
        Date expirationTime = jwtService.extractRefreshTokenExpirationTime(refreshToken);

        // encrypting refresh token before save in db
        String encryptedToken = encryptionService.encrypt(refreshToken);

        // mapping to entity and saving in db
        RefreshTokenEntity refreshTokenEntity = factory.makeRefreshTokenEntity(user, encryptedToken, expirationTime);
        repository.saveAndFlush(refreshTokenEntity);
        log.info("Refresh token for user with email {} was successfully saved", email);
    }

    @Override
    public String getTokenByUserEmail(String email) {

        Optional<RefreshTokenEntity> optionalToken = repository.findByEmail(email);

        if (optionalToken.isEmpty()) {
            log.error("Attempt get not-existent token for user with email {}", email);
            throw new AppException("Token for user with email {" + email + "} doesn't exists", HttpStatus.NOT_FOUND);
        }

        String encryptedToken = optionalToken.get().getToken();
        String decryptedToken = encryptionService.decrypt(encryptedToken);

        return decryptedToken;
    }
}
