package com.ahicode.api.services;

import com.ahicode.api.dtos.AuthorCreationRequestDto;
import com.ahicode.api.dtos.AuthorDto;
import com.ahicode.api.factories.AuthorDtoFactory;
import com.ahicode.api.services.interfaces.AuthorService;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.repositories.AuthorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final RabbitTemplate template;
    private final ObjectMapper objectMapper;
    private final AuthorDtoFactory dtoFactory;
    private final AuthorRepository authorRepository;

    @Override
    public String createAuthorPage(AuthorCreationRequestDto requestDto) {
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

    @Override
    public Page<AuthorDto> getPageAuthors(Pageable pageable) {
        log.info("Attempt to get all authors from db");

        return authorRepository.findAll(pageable)
                .map(dtoFactory::makeAuthorDto);
    }

    @Override
    public AuthorDto getAuthor(Long id) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findById(id);

        if (optionalAuthor.isEmpty()) {
            log.error("Attempt to get non-existent author");
            throw new AppException("Author with id {" + id + "} doesn't exists", HttpStatus.NOT_FOUND);
        }

        return dtoFactory.makeAuthorDto(optionalAuthor.get());
    }

    private void isAuthorKeyUniqueness(String authorKey) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findByKey(authorKey);

        if (optionalAuthor.isPresent()) {
            log.error("Attempt to add an existing author with {} key", authorKey);
            throw new AppException("Author is already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private String makeAuthorKey(AuthorCreationRequestDto requestDto) {
        String firstname = requestDto.getFirstname();
        String lastname = requestDto.getLastname();
        String birthdate = requestDto.getBirthdate();

        return firstname + "-" + lastname + birthdate;
    }
}
