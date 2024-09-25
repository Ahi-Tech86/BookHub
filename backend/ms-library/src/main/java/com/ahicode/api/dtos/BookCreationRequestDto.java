package com.ahicode.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreationRequestDto {
    private String title;
    private String genre;
    private String description;
    @JsonProperty("publication_date")
    private int publicationDate;
}
