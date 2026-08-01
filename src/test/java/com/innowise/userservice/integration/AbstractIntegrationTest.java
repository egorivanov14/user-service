package com.innowise.userservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class AbstractIntegrationTest {

  @Container
  static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

  @Container
  static final GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {

    registry.add(
            "spring.datasource.url",
            postgres::getJdbcUrl);

    registry.add(
            "spring.datasource.username",
            postgres::getUsername);

    registry.add(
            "spring.datasource.password",
            postgres::getPassword);

    registry.add(
            "spring.data.redis.host",
            redis::getHost);

    registry.add(
            "spring.data.redis.port",
            () -> redis.getMappedPort(6379));
  }
}