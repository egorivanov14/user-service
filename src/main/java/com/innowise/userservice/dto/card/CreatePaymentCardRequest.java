package com.innowise.userservice.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreatePaymentCardRequest(
        @NotNull
        Long userId,

        @NotBlank
        @Size(min = 16, max = 16, message = "card number must be 16 symbols")
        String number,

        @NotBlank
        @Size(min = 1, max = 255)
        String holder,

        @NotBlank
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate expirationDate
) {
}