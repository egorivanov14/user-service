package com.innowise.userservice.dto.card;

import java.time.LocalDate;

public record UpdatePaymentCardRequest(
        String holder,
        LocalDate expirationDate
) {
}