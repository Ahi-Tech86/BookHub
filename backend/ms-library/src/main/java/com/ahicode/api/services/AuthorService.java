package com.ahicode.api.services;

import com.ahicode.api.dtos.AuthorCreationRequestDto;

public interface AuthorService {
    String createAuthorPage(AuthorCreationRequestDto requestDto);
}
