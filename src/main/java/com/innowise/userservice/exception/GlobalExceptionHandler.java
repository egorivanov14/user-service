package com.innowise.userservice.exception;

import com.innowise.userservice.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoDataException.class)
  public ResponseEntity<ErrorResponse> handleException(NoDataException exception) {
    int code = HttpStatus.NOT_FOUND.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleException(ConflictException exception) {
    int code = HttpStatus.CONFLICT.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleException(DataIntegrityViolationException exception) {
    int code = HttpStatus.CONFLICT.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleException(AuthenticationException exception) {
    int code = HttpStatus.UNAUTHORIZED.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException exception) {
    int code = HttpStatus.BAD_REQUEST.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException exception) {
    int code = HttpStatus.BAD_REQUEST.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException exception) {
    int code = HttpStatus.BAD_REQUEST.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    String message = exception.getMessage();
    LocalDateTime  now = LocalDateTime.now();
    ErrorResponse errorResponse = new ErrorResponse(code, message, now);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}