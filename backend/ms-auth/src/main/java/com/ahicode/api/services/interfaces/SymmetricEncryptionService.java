package com.ahicode.api.services.interfaces;

public interface SymmetricEncryptionService {
    String encrypt(String data);

    String decrypt(String data);
}
