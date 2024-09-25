package com.ahicode.controllers;

import com.ahicode.api.dtos.AuthorCreationRequestDto;
import com.ahicode.api.dtos.AuthorDto;
import com.ahicode.api.services.AuthorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class AuthorController {

    private final AuthorServiceImpl authorService;

    @GetMapping("/test")
    public String test() {
        return "Please be quite, this is a library";
    }

    @PostMapping("/author/createPage")
    public String createAccount(@RequestBody AuthorCreationRequestDto requestDto) {
        return authorService.createAuthorPage(requestDto);
    }

    @GetMapping("/authors")
    public Page<AuthorDto> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return authorService.getPageAuthors(pageable);
    }

    @GetMapping("/author/{id}")
    public AuthorDto getAuthor(@PathVariable(name = "id") Long id) {
        return authorService.getAuthor(id);
    }
}
