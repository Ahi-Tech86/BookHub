package com.ahicode.api.factories;

import com.ahicode.api.dtos.BookCreationRequestDto;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.entities.BookEntity;
import com.ahicode.storage.enums.BookGenre;
import org.springframework.stereotype.Component;

@Component
public class BookEntityFactory {

    public BookEntity makeBookEntity(BookCreationRequestDto requestDto, AuthorEntity author, BookGenre genre) {
        return BookEntity.builder()
                .title(requestDto.getTitle())
                .author(author)
                .genre(genre)
                .description(requestDto.getDescription())
                .publicationDate(String.valueOf(requestDto.getPublicationDate()))
                .build();
    }
}
