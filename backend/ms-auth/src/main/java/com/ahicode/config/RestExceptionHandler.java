package com.ahicode.config;

import com.ahicode.dtos.ErrorDto;
import com.ahicode.exceptions.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> exceptionHandling(AppException exception) {
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(
                        ErrorDto.builder()
                                .errorMessage(exception.getMessage())
                                .build()
                );
    }
}
