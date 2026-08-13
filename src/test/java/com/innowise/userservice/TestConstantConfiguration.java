package com.innowise.userservice;

import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.entity.PaymentCard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestConstantConfiguration {
  public static final String NAME = "Egor";
  public static final String UPDATE_NAME = "Kirill";
  public static final String SURNAME = "Ivanov";
  public static final String EMAIL = "max203383@gmail.com";
  public static final String UPDATE_EMAIL = "user12345@gmail.com";
  public static final LocalDate BIRTH_DATE = LocalDate.of(2008, 1, 12);
  public static final LocalDateTime CREATED_AT = LocalDateTime.now();
  public static final LocalDateTime UPDATED_AT = LocalDateTime.now();
  public static final Long ID = 1L;
  public static final Boolean ACTIVE = true;
  public static final Boolean INACTIVE = false;
  public static final int ACTIVATED_USER_ROWS = 1;
  public static final int DEACTIVATED_USER_ROWS = 1;
  public static final int ZERO_ROWS = 0;
  public static final Long USER_ID = 1L;
  public static final String CARD_NUMBER = "1234567891234567";
  public static final LocalDate EXPIRE_DATE = LocalDate.of(2026, 12, 31);
  public static final String HOLDER = "Egor Ivanov";
  public static final String NEW_HOLDER = "Kirill Ivanov";
  public static final List<PaymentCardResponse> EMPTY_CARD_RESPONSES_LIST =  new ArrayList<>();
  public static final List<PaymentCard> EMPTY_PAYMENT_CARD_LIST =  new ArrayList<>();
}