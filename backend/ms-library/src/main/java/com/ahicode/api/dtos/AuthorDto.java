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
public class AuthorDto {
    private String firstname;
    private String lastname;
    private String birthdate;
    @JsonProperty("death_date")
    private String deathDate;
}
