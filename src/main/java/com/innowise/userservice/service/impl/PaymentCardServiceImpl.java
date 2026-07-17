package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.OwnerNameAndSurnameFilterRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.specification.PaymentCardSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.innowise.userservice.config.ConstantConfiguration.MAX_CARDS_PER_USER_CONST;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

  private final PaymentCardRepository paymentCardRepository;
  private final PaymentCardMapper paymentCardMapper;
  private final UserRepository userRepository;

  public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, PaymentCardMapper paymentCardMapper, UserRepository userRepository) {
    this.paymentCardRepository = paymentCardRepository;
    this.paymentCardMapper = paymentCardMapper;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public PaymentCardResponse create(CreatePaymentCardRequest createPaymentCardRequest) {
    Long userId = createPaymentCardRequest.userId();
    Optional<User> user = userRepository.findByIdForUpdate(userId);
    if (user.isEmpty()) {
      throw new RuntimeException(); //todo global exception
    }
    Long cardCount = paymentCardRepository.countByUserId(userId);
    if (cardCount < MAX_CARDS_PER_USER_CONST) {
      PaymentCard paymentCard = paymentCardMapper.createPaymentCardRequestToEntity(createPaymentCardRequest);
      PaymentCard savedCard = paymentCardRepository.save(paymentCard);
      return paymentCardMapper.paymentCardToResponse(savedCard);
    } else {
      throw new RuntimeException(); //todo global exception
    }
  }

  @Override
  @Transactional
  public PaymentCardResponse update(Long id, UpdatePaymentCardRequest updatePaymentCardRequest) {
    Optional<PaymentCard> paymentCard = paymentCardRepository.findById(id);
    if (paymentCard.isPresent()) {
      PaymentCard paymentCardEntity = paymentCard.get();
      paymentCardMapper.updatePaymentCard(updatePaymentCardRequest, paymentCardEntity);
      PaymentCard updatedCard = paymentCardRepository.save(paymentCardEntity);
      return paymentCardMapper.paymentCardToResponse(updatedCard);
    } else {
      throw new RuntimeException(); //todo global exception
    }
  }

  @Override
  public PaymentCardResponse findById(Long id) {
    Optional<PaymentCard> paymentCard = paymentCardRepository.findById(id);
    if (paymentCard.isPresent()) {
      PaymentCard paymentCardEntity = paymentCard.get();
      return paymentCardMapper.paymentCardToResponse(paymentCardEntity);
    } else {
      throw new RuntimeException(); //todo global exception
    }
  }

  @Override
  @Transactional
  public void activate(Long id) {
    int countRows = paymentCardRepository.activate(id);
    if (countRows == 0) {
      throw new RuntimeException(); // todo global exception
    }
  }

  @Override
  @Transactional
  public void deactivate(Long id) {
    int countRows = paymentCardRepository.deactivate(id);
    if (countRows == 0) {
      throw new RuntimeException(); // todo global exception
    }
  }

  @Override
  @Transactional
  public void delete(Long id) {
    paymentCardRepository.deleteById(id);
  }

  @Override
  public Page<PaymentCardResponse> findAllByOwnerNameAndSurname(OwnerNameAndSurnameFilterRequest ownerNameAndSurnameFilterRequest, PageRequest pageRequest) {
    String name = ownerNameAndSurnameFilterRequest.name();
    String surname = ownerNameAndSurnameFilterRequest.surname();
    Specification<PaymentCard> specification = Specification.where(
            PaymentCardSpecification.filterByOwnerName(name)
                    .and(PaymentCardSpecification.filterByOwnerSurname(surname)));
    Page<PaymentCard> paymentCards = paymentCardRepository.findAll(specification, pageRequest);
    return paymentCards.map(paymentCardMapper::paymentCardToResponse);
  }

  @Override
  public List<PaymentCardResponse> findAllByUserId(Long userId) {
    List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserId(userId);
    return paymentCards.stream().map(paymentCardMapper::paymentCardToResponse).toList();
  }
}