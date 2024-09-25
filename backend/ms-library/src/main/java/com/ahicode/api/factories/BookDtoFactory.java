package com.ahicode.api.factories;

import com.ahicode.api.dtos.BookDto;
import com.ahicode.storage.entities.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookDtoFactory {

    public BookDto makeBookDto(BookEntity book) {
        return BookDto.builder()
                .title(book.getTitle())
                .description(book.getDescription())
                .genre(book.getGenre())
                .publicationDate(book.getPublicationDate())
                .build();
    }
}
