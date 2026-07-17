package com.innowise.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoDataException.class)
  public ResponseEntity<String> handleException(NoDataException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<String> handleException(ConflictException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
  }
}