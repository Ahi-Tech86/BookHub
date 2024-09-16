package com.ahicode.api.services;

import com.ahicode.api.dtos.AccountActivationRequestDto;
import com.ahicode.api.dtos.CredentialRequestDto;
import com.ahicode.api.dtos.RegistrationRequestDto;
import com.ahicode.api.dtos.UserDto;
import com.ahicode.api.services.interfaces.AuthService;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.RegistrationRequest;
import com.ahicode.storage.entities.UserEntity;
import com.ahicode.storage.repositories.RegistrationRequestRepository;
import com.ahicode.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RegistrationRequestRepository requestRepository;

    @Override
    public String registerUser(RegistrationRequestDto request) {
        String email = request.getEmail();

        // checking email on uniqueness
        isEmailUniqueness(email);

        // generating activation code
        String activationCode = generateActivationCode();

        // creating mongo document
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email(email)
                .dto(request)
                .activationCode(activationCode)
                .createAt(Instant.now())
                .build();

        // temporary saving information
        requestRepository.save(registrationRequest);
        log.info("User information with email {} is temporarily saved", email);

        // trying to send message on email
        try {
            emailService.sendActivationEmail(email, activationCode);
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw new AppException("There was an error sending the message", HttpStatus.BAD_REQUEST);
        }

        return "An activation code has been sent to your email, please send the activation code before it expires. " +
                "The activation code expires in 20 minutes.";
    }

    @Override
    public String activateAccount(AccountActivationRequestDto request) {
        return "";
    }

    @Override
    public UserDto login(CredentialRequestDto request) {
        return null;
    }

    private String generateActivationCode() {
        Random random = new Random();

        int number = 1 + random.nextInt(1000000);

        return String.format("%06d", number);
    }

    private void isEmailUniqueness(String email) {

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            log.error("Attempt to register with an existing email: {}", email);
            throw new AppException("User with email {" + email + "} is already exists", HttpStatus.BAD_REQUEST);
        }
    }
}
