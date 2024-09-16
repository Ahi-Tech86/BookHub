package com.ahicode.api.services;

import com.ahicode.api.dtos.*;
import com.ahicode.api.factories.TemporaryUserDtoFactory;
import com.ahicode.api.services.interfaces.AuthService;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.UserEntity;
import com.ahicode.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TemporaryUserDtoFactory temporaryUserDtoFactory;

    private final RedisTemplate<String, TemporaryUserDto> redisTemplate;

    @Override
    public String registerUser(RegistrationRequestDto request) {
        String email = request.getEmail();

        // checking email on uniqueness
        isEmailUniqueness(email);

        // generating activation code
        String activationCode = generateActivationCode();

        // saving temporary information about user
        TemporaryUserDto temporaryUserDto = temporaryUserDtoFactory.makeTemporaryUserDto(request, activationCode);
        redisTemplate.opsForValue().set(email, temporaryUserDto, 20, TimeUnit.MINUTES);
        log.info("User information with email {} is temporarily saved", email);

        // trying to send message on email
        try {
            emailService.sendActivationEmail(email, activationCode);
        } catch (RuntimeException exception) {
            log.error("Attempt to send message was unsuccessful", exception);
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
