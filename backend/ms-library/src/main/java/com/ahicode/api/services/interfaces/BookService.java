package com.ahicode.api.services.interfaces;

import com.ahicode.api.dtos.BookCreationRequestDto;
import com.ahicode.api.dtos.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(Long authorId,BookCreationRequestDto requestDto);
    List<BookDto> getAllAuthorsBooks(Long authorId);
    BookDto getBook(Long authorId, Long bookId);
}
