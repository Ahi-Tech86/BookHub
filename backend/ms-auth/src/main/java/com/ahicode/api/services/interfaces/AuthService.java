package com.ahicode.api.services.interfaces;

import com.ahicode.api.dtos.AccountActivationRequestDto;
import com.ahicode.api.dtos.CredentialRequestDto;
import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.UserDto;

import java.util.List;

public interface AuthService {

    String registerUser(RegistrationRequestDto request);

    UserDto activateAccount(AccountActivationRequestDto request);

    List<Object> login(CredentialRequestDto request);
}
