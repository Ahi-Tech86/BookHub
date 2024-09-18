package com.ahicode.api.services;

import com.ahicode.api.dtos.*;
import com.ahicode.api.factories.TemporaryUserDtoFactory;
import com.ahicode.api.factories.UserDtoFactory;
import com.ahicode.api.factories.UserEntityFactory;
import com.ahicode.api.services.interfaces.AuthService;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.UserEntity;
import com.ahicode.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final TokenServiceImpl tokenService;
    private final UserDtoFactory userDtoFactory;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityFactory userEntityFactory;
    private final TemporaryUserDtoFactory temporaryUserDtoFactory;

    private final RedisTemplate<String, TemporaryUserDto> redisTemplate;

    @Override
    public String registerUser(RegistrationRequestDto requestDto) {
        String email = requestDto.getEmail();

        // validation email on email pattern
        if (!isEmailValid(email)) {
            log.error("Attempt register with invalid email {}", email);
            throw new AppException("Email does not match pattern", HttpStatus.BAD_REQUEST);
        }

        // checking email on uniqueness
        isEmailUniqueness(email);

        // generating activation code
        String activationCode = generateActivationCode();

        // saving temporary information about user
        TemporaryUserDto temporaryUserDto = temporaryUserDtoFactory.makeTemporaryUserDto(requestDto, activationCode);
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
    @Transactional
    public UserDto activateAccount(AccountActivationRequestDto requestDto) {
        String email = requestDto.getEmail();
        String activationCode = requestDto.getActivationCode();

        // getting temporary information about user from redis
        TemporaryUserDto temporaryUserDto = redisTemplate.opsForValue().get(email);

        // comparison of generated code and code to sent by the user
        if (!activationCode.equals(temporaryUserDto.getActivationCode())) {
            log.error("Attempting to activate an account with an incorrect activation code to email {}", email);
            throw new AppException("The activation code doesn't match what the server generated", HttpStatus.UNAUTHORIZED);
        }

        // mapping dto to entity and encoding password
        UserEntity user = userEntityFactory.makeUserEntity(temporaryUserDto);
        user.setPassword(passwordEncoder.encode(temporaryUserDto.getPassword()));

        UserEntity savedUser = userRepository.saveAndFlush(user);
        log.info("User with email {} was successfully saved", email);

        tokenService.createAndSaveToken(user);

        return userDtoFactory.makeUserDto(savedUser);
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

    private boolean isEmailValid(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        if (email == null || email.isEmpty()) {
            log.error("Attempt to make request with invalid email: {}", email);
            throw new AppException("Email is missing", HttpStatus.BAD_REQUEST);
        }

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void isEmailUniqueness(String email) {

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            log.error("Attempt to register with an existing email: {}", email);
            throw new AppException("User with email {" + email + "} is already exists", HttpStatus.BAD_REQUEST);
        }
    }
}
