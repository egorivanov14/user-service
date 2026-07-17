package com.innowise.userservice.dto.card;

import java.time.LocalDate;

public record UpdatePaymentCardRequest(
        String number,
        String holder,
        LocalDate expirationDate
) {
}