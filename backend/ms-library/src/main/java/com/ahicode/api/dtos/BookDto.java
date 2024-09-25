package com.ahicode.api.dtos;

import com.ahicode.storage.enums.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String title;
    private BookGenre genre;
    private String description;
    private String publicationDate;
}
