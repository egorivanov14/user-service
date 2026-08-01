package com.innowise.userservice.controller;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.FilterByOwnerNameAndSurnameRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-cards")
public class PaymentCardController {

  private final PaymentCardService paymentCardService;

  public PaymentCardController(PaymentCardService paymentCardService) {
    this.paymentCardService = paymentCardService;
  }

  @PostMapping("/create")
  public ResponseEntity<PaymentCardResponse> create(@Valid @RequestBody CreatePaymentCardRequest createPaymentCardRequest) {
    PaymentCardResponse paymentCardResponse = paymentCardService.create(createPaymentCardRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardResponse);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<PaymentCardResponse> update(@PathVariable Long id, @Valid @RequestBody UpdatePaymentCardRequest updatePaymentCardRequest) {
    PaymentCardResponse paymentCardResponse = paymentCardService.update(id, updatePaymentCardRequest);
    return ResponseEntity.ok(paymentCardResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardResponse> findById(@PathVariable Long id) {
    PaymentCardResponse paymentCardResponse = paymentCardService.findById(id);
    return ResponseEntity.ok(paymentCardResponse);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/activate/{id}")
  public ResponseEntity<Void> activate(@PathVariable Long id) {
    paymentCardService.activate(id);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/deactivate/{id}")
  public ResponseEntity<Void> deactivate(@PathVariable Long id) {
    paymentCardService.deactivate(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    paymentCardService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/by-user/{id}")
  public ResponseEntity<List<PaymentCardResponse>> findAllByUserId(@PathVariable Long id) {
    List<PaymentCardResponse> paymentCardResponseList = paymentCardService.findAllByUserId(id);
    return ResponseEntity.ok(paymentCardResponseList);
  }

  @PostMapping("/by-user/name-surname-filter")
  public ResponseEntity<Page<PaymentCardResponse>> findAllByUserNameAndSurname(@Valid @RequestBody FilterByOwnerNameAndSurnameRequest filterByOwnerNameAndSurnameRequest, Pageable pageable) {
    Page<PaymentCardResponse> paymentCardResponseList = paymentCardService.findAllByOwnerNameAndSurname(filterByOwnerNameAndSurnameRequest, pageable);
    return ResponseEntity.ok(paymentCardResponseList);
  }
}