package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 1, max = 100, message = "invalid length of parameter")
        String name,

        @NotBlank
        @Size(min = 1, max = 100, message = "invalid length of parameter")
        String surname,

        @NotNull
        @Past
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate,

        @Email(message = "invalid format of email")
        String email
) {
}