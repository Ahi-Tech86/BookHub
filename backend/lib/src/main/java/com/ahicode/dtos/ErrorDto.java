package com.ahicode.dtos;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class ErrorDto {
    private String errorMessage;
}
