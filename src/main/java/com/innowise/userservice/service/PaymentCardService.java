package com.innowise.userservice.service;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.FilterByOwnerNameAndSurnameRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

  PaymentCardResponse create(CreatePaymentCardRequest createPaymentCardRequest);

  PaymentCardResponse update(Long id, UpdatePaymentCardRequest updatePaymentCardRequest);

  PaymentCardResponse findById(Long id);

  void activate(Long id);

  void deactivate(Long id);

  void delete(Long id);

  void evictCache(Long id);

  List<PaymentCardResponse> findAllByUserId(Long userId);

  Page<PaymentCardResponse> findAllByOwnerNameAndSurname(FilterByOwnerNameAndSurnameRequest filterByOwnerNameAndSurnameRequest, Pageable pageable);
}