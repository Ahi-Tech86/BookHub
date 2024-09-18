package com.ahicode.config;

import com.ahicode.dtos.ErrorDto;
import com.ahicode.exceptions.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class LibraryRestExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {AppException.class})
    public ResponseEntity<ErrorDto> exceptionHandler(AppException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorDto.builder().errorMessage(e.getMessage()).build());
    }
}
