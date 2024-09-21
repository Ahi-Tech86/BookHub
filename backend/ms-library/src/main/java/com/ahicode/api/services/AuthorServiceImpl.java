package com.ahicode.api.services;

import com.ahicode.api.dtos.CreateAuthorPageRequestDto;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.repositories.AuthorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final RabbitTemplate template;
    private final ObjectMapper objectMapper;
    private final AuthorRepository authorRepository;

    @Override
    public String createAuthorPage(CreateAuthorPageRequestDto requestDto) {
        // making authorKey and checking it on uniqueness
        String authorKey = makeAuthorKey(requestDto);
        isAuthorKeyUniqueness(authorKey);

        // serializing and sending request to queue
        try {
            String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
            template.convertAndSend("author_exchange", "author.routing.key", jsonRequestBody);
            log.info("Message {} was successfully send in queue", jsonRequestBody);
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while serializing and sending the request {}", requestDto.toString());
            throw new AppException("An internal server error occurred, please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return "Your request has been accepted and will be processed after some time";
    }

    private void isAuthorKeyUniqueness(String authorKey) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findByKey(authorKey);

        if (optionalAuthor.isPresent()) {
            log.error("Attempt to add an existing author with {} key", authorKey);
            throw new AppException("Author is already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private String makeAuthorKey(CreateAuthorPageRequestDto requestDto) {
        String firstname = requestDto.getFirstname();
        String lastname = requestDto.getLastname();
        String birthdate = requestDto.getBirthdate();

        return firstname + "-" + lastname + birthdate;
    }
}
