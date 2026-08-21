package com.innowise.userservice.exception;

public class AuthenticationServiceException extends RuntimeException {
  public AuthenticationServiceException(String message) {
    super(message);
  }
}