package com.ahicode.api.factories;

import com.ahicode.api.dtos.AuthorDto;
import com.ahicode.storage.entities.AuthorEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorDtoFactory {

    public AuthorDto makeAuthorDto(AuthorEntity author) {
        return AuthorDto.builder()
                .firstname(author.getFirstname())
                .lastname(author.getLastname())
                .birthdate(author.getBirthdate())
                .deathDate(author.getDeathdate())
                .build();
    }
}
