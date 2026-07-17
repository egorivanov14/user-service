package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 1, max = 100, message = "invalid length of parameter")
        String name,

        @NotBlank
        @Size(min = 1, max = 100, message = "invalid length of parameter")
        String surname,

        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate,

        @Email(message = "invalid format of email")
        String email
) {
}