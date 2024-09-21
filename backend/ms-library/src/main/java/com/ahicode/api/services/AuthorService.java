package com.ahicode.api.services;

import com.ahicode.api.dtos.CreateAuthorPageRequestDto;

public interface AuthorService {
    String createAuthorPage(CreateAuthorPageRequestDto requestDto);
}
