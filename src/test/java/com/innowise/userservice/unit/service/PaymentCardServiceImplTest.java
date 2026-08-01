package com.innowise.userservice.unit.service;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.FilterByOwnerNameAndSurnameRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ConflictException;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.innowise.userservice.TestConstantConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

  @Mock
  private PaymentCardRepository paymentCardRepository;

  @Mock
  private PaymentCardMapper paymentCardMapper;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private PaymentCardServiceImpl paymentCardService;

  @Test
  void create_shouldCreateCard() {

    CreatePaymentCardRequest request =
            new CreatePaymentCardRequest(USER_ID, CARD_NUMBER, HOLDER, EXPIRE_DATE);

    User user = new User();
    PaymentCard card = new PaymentCard();
    PaymentCardResponse response =
            new PaymentCardResponse(ID, USER_ID, HOLDER, EXPIRE_DATE, ACTIVE, CREATED_AT, UPDATED_AT);

    when(userRepository.findByIdForUpdate(USER_ID))
            .thenReturn(Optional.of(user));

    when(paymentCardRepository.existsByNumber(CARD_NUMBER))
            .thenReturn(false);

    when(paymentCardRepository.countByUserIdAndActiveIsTrue(USER_ID))
            .thenReturn(2L);

    when(paymentCardMapper.createPaymentCardRequestToEntity(request))
            .thenReturn(card);

    when(paymentCardRepository.save(card))
            .thenReturn(card);

    when(paymentCardMapper.paymentCardToResponse(card))
            .thenReturn(response);

    PaymentCardResponse actual = paymentCardService.create(request);

    assertEquals(response, actual);
  }

  @Test
  void create_userNotFound_shouldThrowNoDataException() {

    CreatePaymentCardRequest request =
            new CreatePaymentCardRequest(USER_ID, CARD_NUMBER, HOLDER, EXPIRE_DATE);

    when(userRepository.findByIdForUpdate(USER_ID))
            .thenReturn(Optional.empty());

    assertThrows(NoDataException.class,
            () -> paymentCardService.create(request));

    verify(paymentCardRepository, never()).save(any());
  }

  @Test
  void create_duplicateNumber_shouldThrowConflictException() {

    CreatePaymentCardRequest request =
            new CreatePaymentCardRequest(USER_ID, CARD_NUMBER, HOLDER, EXPIRE_DATE);

    when(userRepository.findByIdForUpdate(USER_ID))
            .thenReturn(Optional.of(new User()));

    when(paymentCardRepository.existsByNumber(CARD_NUMBER))
            .thenReturn(true);

    assertThrows(ConflictException.class,
            () -> paymentCardService.create(request));

    verify(paymentCardRepository, never()).save(any());
  }

  @Test
  void create_limitExceeded_shouldThrowConflictException() {

    CreatePaymentCardRequest request =
            new CreatePaymentCardRequest(USER_ID, CARD_NUMBER, HOLDER, EXPIRE_DATE);

    when(userRepository.findByIdForUpdate(USER_ID))
            .thenReturn(Optional.of(new User()));

    when(paymentCardRepository.existsByNumber(CARD_NUMBER))
            .thenReturn(false);

    when(paymentCardRepository.countByUserIdAndActiveIsTrue(USER_ID))
            .thenReturn(5L);

    assertThrows(ConflictException.class,
            () -> paymentCardService.create(request));

    verify(paymentCardRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateCard() {

    UpdatePaymentCardRequest request =
            new UpdatePaymentCardRequest(NEW_CARD_NUMBER, HOLDER, EXPIRE_DATE);

    PaymentCard card = new PaymentCard();

    PaymentCardResponse response =
            new PaymentCardResponse(ID, USER_ID, HOLDER, EXPIRE_DATE, ACTIVE, CREATED_AT, UPDATED_AT);

    when(paymentCardRepository.findById(ID))
            .thenReturn(Optional.of(card));

    when(paymentCardRepository.existsByNumber(NEW_CARD_NUMBER))
            .thenReturn(false);

    doNothing().when(paymentCardMapper)
            .updatePaymentCard(request, card);

    when(paymentCardRepository.save(card))
            .thenReturn(card);

    when(paymentCardMapper.paymentCardToResponse(card))
            .thenReturn(response);

    PaymentCardResponse actual = paymentCardService.update(ID, request);

    assertEquals(response, actual);
  }

  @Test
  void update_cardNotFound_shouldThrowNoDataException() {

    UpdatePaymentCardRequest request =
            new UpdatePaymentCardRequest(NEW_CARD_NUMBER, HOLDER, EXPIRE_DATE);

    when(paymentCardRepository.findById(ID))
            .thenReturn(Optional.empty());

    assertThrows(NoDataException.class,
            () -> paymentCardService.update(ID, request));
  }

  @Test
  void update_duplicateNumber_shouldThrowConflictException() {

    UpdatePaymentCardRequest request =
            new UpdatePaymentCardRequest(NEW_CARD_NUMBER, HOLDER, EXPIRE_DATE);

    when(paymentCardRepository.findById(ID))
            .thenReturn(Optional.of(new PaymentCard()));

    when(paymentCardRepository.existsByNumber(NEW_CARD_NUMBER))
            .thenReturn(true);

    assertThrows(ConflictException.class,
            () -> paymentCardService.update(ID, request));
  }

  @Test
  void findById_shouldReturnCard() {

    PaymentCard card = new PaymentCard();

    PaymentCardResponse response =
            new PaymentCardResponse(ID, USER_ID, HOLDER, EXPIRE_DATE, ACTIVE, CREATED_AT, UPDATED_AT);

    when(paymentCardRepository.findById(ID))
            .thenReturn(Optional.of(card));

    when(paymentCardMapper.paymentCardToResponse(card))
            .thenReturn(response);

    PaymentCardResponse actual = paymentCardService.findById(ID);

    assertEquals(response, actual);
  }

  @Test
  void findById_shouldThrowNoDataException() {

    when(paymentCardRepository.findById(ID))
            .thenReturn(Optional.empty());

    assertThrows(NoDataException.class,
            () -> paymentCardService.findById(ID));
  }

  @Test
  void activate_shouldActivateCard() {

    when(paymentCardRepository.activate(ID)).thenReturn(1);

    paymentCardService.activate(ID);

    verify(paymentCardRepository).activate(ID);
  }

  @Test
  void activate_shouldThrowNoDataException() {

    when(paymentCardRepository.activate(ID)).thenReturn(0);

    assertThrows(NoDataException.class,
            () -> paymentCardService.activate(ID));
  }

  @Test
  void deactivate_shouldDeactivateCard() {

    when(paymentCardRepository.deactivate(ID)).thenReturn(1);

    paymentCardService.deactivate(ID);

    verify(paymentCardRepository).deactivate(ID);
  }

  @Test
  void deactivate_shouldThrowNoDataException() {

    when(paymentCardRepository.deactivate(ID)).thenReturn(0);

    assertThrows(NoDataException.class,
            () -> paymentCardService.deactivate(ID));
  }

  @Test
  void delete_shouldDeleteCard() {

    doNothing().when(paymentCardRepository).deleteById(ID);

    paymentCardService.delete(ID);

    verify(paymentCardRepository).deleteById(ID);
  }

  @Test
  void findAllByUserId_shouldReturnCards() {

    PaymentCard card = new PaymentCard();

    PaymentCardResponse response =
            new PaymentCardResponse(ID, USER_ID, HOLDER, EXPIRE_DATE, ACTIVE, CREATED_AT, UPDATED_AT);

    when(paymentCardRepository.findAllByUserId(USER_ID))
            .thenReturn(List.of(card));

    when(paymentCardMapper.paymentCardToResponse(card))
            .thenReturn(response);

    List<PaymentCardResponse> result =
            paymentCardService.findAllByUserId(USER_ID);

    assertEquals(1, result.size());
    assertEquals(response, result.getFirst());
  }

  @Test
  void findAllByOwnerNameAndSurname_shouldReturnCards() {

    Pageable pageable = PageRequest.of(0, 10);

    FilterByOwnerNameAndSurnameRequest request =
            new FilterByOwnerNameAndSurnameRequest(NAME, SURNAME);

    PaymentCard card = new PaymentCard();

    PaymentCardResponse response =
            new PaymentCardResponse(ID, USER_ID, HOLDER, EXPIRE_DATE, ACTIVE, CREATED_AT, UPDATED_AT);

    Page<PaymentCard> cards = new PageImpl<>(List.of(card));

    when(paymentCardRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(cards);

    when(paymentCardMapper.paymentCardToResponse(card))
            .thenReturn(response);

    Page<PaymentCardResponse> result =
            paymentCardService.findAllByOwnerNameAndSurname(request, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(response, result.getContent().getFirst());

    verify(paymentCardRepository).findAll(any(Specification.class), eq(pageable));
  }
}