package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UpdateUserRequest(
        String name,
        String surname,
        LocalDate birthDate,

        @Email
        String email
) {
}