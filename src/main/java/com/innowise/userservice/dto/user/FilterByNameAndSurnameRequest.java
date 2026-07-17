package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FilterByNameAndSurnameRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
        @NotBlank
        @Size(min = 1, max = 100)
        String surname
) {
}