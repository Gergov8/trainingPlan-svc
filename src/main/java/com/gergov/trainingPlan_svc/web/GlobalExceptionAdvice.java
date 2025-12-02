package com.gergov.trainingPlan_svc.web;

import com.gergov.trainingPlan_svc.web.dto.ErrorResponse;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ErrorResponse> handleAiException(NonTransientAiException e) {

        ErrorResponse dto = new ErrorResponse(LocalDateTime.now(), e.getMessage());

        return  ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(dto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        ErrorResponse dto = new ErrorResponse(LocalDateTime.now(), e.getMessage());

        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(dto);
    }
}