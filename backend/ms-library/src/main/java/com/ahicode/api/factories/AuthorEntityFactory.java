package com.ahicode.api.factories;

import com.ahicode.storage.entities.AuthorEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorEntityFactory {

    public AuthorEntity makeAuthorEntity(String firstname, String lastname, String birthdate, String deathdate) {
        return AuthorEntity.builder()
                .firstname(firstname.toLowerCase())
                .lastname(lastname.toLowerCase())
                .birthdate(birthdate)
                .deathdate(deathdate)
                .key(firstname + "-" + lastname + birthdate)
                .build();
    }
}
