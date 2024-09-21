package com.ahicode.api.dtos.open_lib_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorDto {
    private String name;
    @JsonProperty("birth_date")
    private String birthDate;
    @JsonProperty("death_date")
    private String deathDate;
}
