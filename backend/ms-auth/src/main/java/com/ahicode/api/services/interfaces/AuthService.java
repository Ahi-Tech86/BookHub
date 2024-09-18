package com.ahicode.api.services.interfaces;

import com.ahicode.api.dtos.AccountActivationRequestDto;
import com.ahicode.api.dtos.CredentialRequestDto;
import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.UserDto;

public interface AuthService {

    String registerUser(RegistrationRequestDto request);

    UserDto activateAccount(AccountActivationRequestDto request);

    UserDto login(CredentialRequestDto request);
}
