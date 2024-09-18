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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpirationTime;

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
        Date expirationTime = new Date(System.currentTimeMillis() + refreshTokenExpirationTime);

        // encrypting refresh token before save in db
        String encryptedToken = encryptionService.encrypt(refreshToken);

        // mapping to entity and saving in db
        RefreshTokenEntity refreshTokenEntity = factory.makeRefreshTokenEntity(user, encryptedToken, expirationTime);
        repository.saveAndFlush(refreshTokenEntity);
        log.info("Refresh token for user with email {} was successfully saved", email);
    }

    @Override
    @Transactional
    public String getTokenByUserEmail(String email) {

        Logger logger = LoggerFactory.getLogger(getClass());

        try {
            RefreshTokenEntity refreshTokenEntity = repository.findByEmail(email).orElseThrow(
                    () -> new AppException("Token for user with email {" + email + "} doesn't exists", HttpStatus.NOT_FOUND)
            );

            String encryptedToken = refreshTokenEntity.getToken();
            String decryptedToken = encryptionService.decrypt(encryptedToken);

            if (jwtService.isRefreshTokenExpired(decryptedToken)) {
                String newRefreshToken = jwtService.generateRefreshToken(email, jwtService.extractRole(decryptedToken));
                String encryptedNewToken = encryptionService.encrypt(newRefreshToken);
                refreshTokenEntity.setToken(encryptedNewToken);
                refreshTokenEntity.setCreateAt(new Date(System.currentTimeMillis()));
                refreshTokenEntity.setExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationTime));
                repository.saveAndFlush(refreshTokenEntity);
                log.info("Refresh token was updated for {} user", email);
                return newRefreshToken;
            }

            return decryptedToken;
        } catch (AppException exception) {
            logger.error("Attempt to get token for unauthorized email {}", email);
            throw exception;
        } catch (Exception exception) {
            logger.error("Unexpected error occurred while retrieving token for email {}: {}", email, exception.getMessage(), exception);
            throw new AppException("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
