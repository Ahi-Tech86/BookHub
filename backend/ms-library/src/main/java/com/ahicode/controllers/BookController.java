package com.ahicode.controllers;

import com.ahicode.api.dtos.AuthorCreationRequestDto;
import com.ahicode.api.services.AuthorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class BookController {

    private final AuthorServiceImpl authorService;

    @GetMapping("/test")
    public String test() {
        return "Please be quite, this is a library";
    }

    @PostMapping("/author/createPage")
    public String createAccount(@RequestBody AuthorCreationRequestDto requestDto) {
        return authorService.createAuthorPage(requestDto);
    }
}
