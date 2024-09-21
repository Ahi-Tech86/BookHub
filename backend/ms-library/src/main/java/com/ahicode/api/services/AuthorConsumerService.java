package com.ahicode.api.services;

import com.ahicode.api.dtos.AuthorCreationRequestDto;
import com.ahicode.api.dtos.open_lib_api.AuthorDto;
import com.ahicode.api.dtos.open_lib_api.AuthorsResponse;
import com.ahicode.api.factories.AuthorEntityFactory;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.repositories.AuthorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorConsumerService {

    private final ObjectMapper objectMapper;
    private final AuthorEntityFactory factory;
    private final AuthorRepository authorRepository;

    @RabbitListener(queues = "authors_queue")
    public void receiveMessage(String jsonRequestBody) {
        log.info("The message {} was delivered", jsonRequestBody);

        try {
            AuthorCreationRequestDto requestDto = objectMapper.readValue(jsonRequestBody, AuthorCreationRequestDto.class);
            String authorName = requestDto.getFirstname() + " " + requestDto.getLastname();
            String birthdate = requestDto.getBirthdate();

            AuthorDto authorDto = fetchAuthorFromApi(authorName);
            if (authorDto != null) {
                saveAuthor(authorDto, birthdate);
            }
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while serializing and sending the request {}", jsonRequestBody);
        } catch (Exception exception) {
            log.error("Error receiving message: {}", jsonRequestBody, exception);
        }
    }

    private AuthorDto fetchAuthorFromApi(String authorName) throws IOException {
        String urlString = buildAuthorSearchUrl(authorName);
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            log.warn("Failed to fetch author: {}", authorName);
        }

        AuthorsResponse authorsResponse = objectMapper.readValue(connection.getInputStream(), AuthorsResponse.class);
        if (authorsResponse.getNumFound() == 0) {
            log.info("Author {} not found", authorName);
            return null;
        }

        return authorsResponse.getDocs().get(0);
    }

    @Transactional
    private void saveAuthor(AuthorDto authorDto, String birthdate) {
        String[] nameParts = authorDto.getName().split(" ");
        String firstname = nameParts[0];
        String lastname = nameParts[1];
        String deathDate = extractDate(authorDto.getDeathDate());

        AuthorEntity author = factory.makeAuthorEntity(firstname, lastname, birthdate, deathDate);
        authorRepository.saveAndFlush(author);
        log.info("New author {} was successfully saved in db", author.getKey());
    }

    private String buildAuthorSearchUrl(String authorName) throws UnsupportedEncodingException {
        String encodedAuthorName = URLEncoder.encode(authorName, StandardCharsets.UTF_8.toString());
        return "https://openlibrary.org/search/authors.json?q=" + encodedAuthorName;
    }

    private String extractYear(String authorDate) {

        if (authorDate == null || authorDate.trim().isEmpty()) {
            log.error("Death date is null or empty");
            return "-";
        }

        String[] formats = {
                "d MMMM yyyy",
                "yyyy",
                "MMMM yyyy",
                "d MMM yyyy"
        };

        for (String format : formats) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

            try {
                simpleDateFormat.setLenient(false);
                Date date = simpleDateFormat.parse(authorDate);
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                return yearFormat.format(date);
            } catch (ParseException exception) {
                log.error("An error occurred while parsing date {}", authorDate);
                throw new AppException("An internal server error occurred, please try again",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        log.error("Failed to parse death: {}", authorDate);
        throw new AppException("Invalid date format for deathDate", HttpStatus.BAD_REQUEST);
    }

    private String extractDate(String authorDate) {
        String regex = "\\d{4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(authorDate);

        if (matcher.find()) {
            return matcher.group();
        }

        return "-";
    }
}
