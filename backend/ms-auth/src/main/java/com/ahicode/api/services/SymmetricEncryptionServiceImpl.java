package com.ahicode.api.services;

import com.ahicode.api.services.interfaces.SymmetricEncryptionService;
import com.ahicode.exceptions.AppException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class SymmetricEncryptionServiceImpl implements SymmetricEncryptionService {

    @Value("${application.security.encryption.secret-key}")
    private String stringSecretKey;

    private SecretKeySpec secretKey;
    private IvParameterSpec ivParameterSpec;

    @PostConstruct
    private void init() {
        byte[] key = Base64.getDecoder().decode(stringSecretKey);

        if (key.length != 32) {
            log.error("The encryption key doesn't match the required length");
            throw new IllegalArgumentException("Invalid key length for AES_256");
        }

        this.secretKey = new SecretKeySpec(key, "AES");
        byte[] iv = new byte[16];
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    @Override
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception exception) {
            log.error("Error while encrypting data with AES algorithm");
            throw new AppException("An error occurred while encrypting data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception exception) {
            log.error("Error while decrypting encrypted data with AES algorithm");
            throw new AppException("An error occurred while decrypting data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
