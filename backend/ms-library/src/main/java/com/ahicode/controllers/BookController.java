package com.ahicode.controllers;

import com.ahicode.api.dtos.BookCreationRequestDto;
import com.ahicode.api.dtos.BookDto;
import com.ahicode.api.services.BookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class BookController {

    private final BookServiceImpl service;

    @PostMapping("/author/{authorId}/createBook")
    public BookDto createBook(@PathVariable Long authorId, @RequestBody BookCreationRequestDto requestDto) {
        return service.createBook(authorId, requestDto);
    }
}
