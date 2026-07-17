package com.innowise.userservice.dto.card;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentCardResponse(
        Long id,
        Long userId,
        String holder,
        LocalDate expirationDate,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}