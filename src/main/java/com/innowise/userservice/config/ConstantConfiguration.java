package com.innowise.userservice.config;

import io.grpc.Metadata;

public class ConstantConfiguration {
  public static final String NAME_CONST = "name";
  public static final String SURNAME_CONST = "surname";
  public static final String USER_CONST = "user";
  public static final int MAX_CARDS_PER_USER_CONST = 5;
  public static final String ROLE_CONST = "role";
  public static final String SERVICE_SECRET_KEY_NAME_CONST = "serviceSecretKey";
  public static final Metadata.Key<String> SERVICE_SECRET_KEY_CONST =
          Metadata.Key.of(SERVICE_SECRET_KEY_NAME_CONST, Metadata.ASCII_STRING_MARSHALLER);
}