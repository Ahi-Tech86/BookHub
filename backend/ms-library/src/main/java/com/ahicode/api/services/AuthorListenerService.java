package com.ahicode.api.services;

import com.ahicode.api.dtos.CreateAuthorPageRequestDto;
import com.ahicode.api.dtos.open_lib_api.AuthorDto;
import com.ahicode.api.dtos.open_lib_api.AuthorResponse;
import com.ahicode.api.factories.AuthorEntityFactory;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.repositories.AuthorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorListenerService {

    private final ObjectMapper objectMapper;
    private final AuthorEntityFactory factory;
    private final AuthorRepository authorRepository;

    @RabbitListener(queues = "authors_queue")
    public void receiveMessage(String jsonRequestBody) throws Exception{
        log.info("The message {} was delivered", jsonRequestBody);

        String name;
        String birthdate;
        try {
            CreateAuthorPageRequestDto requestDto = objectMapper.readValue(jsonRequestBody, CreateAuthorPageRequestDto.class);
            name = requestDto.getFirstname() + " " + requestDto.getLastname();
            birthdate = requestDto.getBirthdate();
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while serializing and sending the request {}", jsonRequestBody);
            throw new AppException("An internal server error occurred, please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        fetchAndSaveAuthor(name, birthdate);
    }

    private void fetchAndSaveAuthor(String authorName, String birthdate) throws Exception {
        log.info(authorName);
        String encodedAuthorName = URLEncoder.encode(authorName, StandardCharsets.UTF_8.toString());
        String urlString = "https://openlibrary.org/search/authors.json?q=" + encodedAuthorName;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        AuthorDto authorDto = null;
        if (connection.getResponseCode() == 200) {
            AuthorResponse authorResponse = objectMapper.readValue(connection.getInputStream(), AuthorResponse.class);

            if (authorResponse.getNumFound() == 0) {
                log.info("Author {} didn't found", authorName);
                return;
            }

            authorDto = authorResponse.getDocs().get(0);
        }

        String[] nameParts = authorDto.getName().split(" ");
        String firstname = nameParts[0];
        String lastname = nameParts[1];
        String deathdate = authorDto.getDeathDate();

        AuthorEntity author = factory.makeAuthorEntity(firstname, lastname, birthdate, deathdate);
        authorRepository.saveAndFlush(author);
        log.info("New author {} was successfully saved in db", author.getKey());
    }

    private String extractYear(String birthdate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy");

        try {
            Date date = simpleDateFormat.parse(birthdate);
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            return yearFormat.format(date);
        } catch (ParseException exception) {
            log.error("An error occurred while parsing date {}", birthdate);
            throw new AppException("An internal server error occurred, please try again",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
