package com.ahicode.controllers;

import com.ahicode.api.dtos.BookCreationRequestDto;
import com.ahicode.api.dtos.BookDto;
import com.ahicode.api.services.BookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class BookController {

    private final BookServiceImpl service;

    @PostMapping("/author/{authorId}/createBook")
    public BookDto createBook(@PathVariable Long authorId, @RequestBody BookCreationRequestDto requestDto) {
        return service.createBook(authorId, requestDto);
    }

    @GetMapping("/author/{authorId}/books")
    public List<BookDto> getAuthorsBooks(@PathVariable Long authorId) {
        return service.getAllAuthorsBooks(authorId);
    }

    @GetMapping("/author/{authorId}/{bookId}")
    public BookDto getBook(@PathVariable Long authorId, @PathVariable Long bookId) {
        return service.getBook(authorId, bookId);
    }
}
