package com.ahicode.api.services.interfaces;

import com.ahicode.api.dtos.AuthorCreationRequestDto;
import com.ahicode.api.dtos.AuthorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    String createAuthorPage(AuthorCreationRequestDto requestDto);
    AuthorDto getAuthor(Long id);
    Page<AuthorDto> getPageAuthors(Pageable pageable);
}
