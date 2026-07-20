package com.innowise.userservice.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FilterByOwnerNameAndSurnameRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
        @NotBlank
        @Size(min = 1, max = 100)
        String surname
) {
}