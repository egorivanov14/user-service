package com.innowise.userservice.service.impl;

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
import com.innowise.userservice.security.HashService;
import com.innowise.userservice.service.CacheService;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.specification.PaymentCardSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final HashService hashService;
  private final CacheService cacheService;

  public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, PaymentCardMapper paymentCardMapper, UserRepository userRepository, HashService hashService, CacheService cacheService) {
    this.paymentCardRepository = paymentCardRepository;
    this.paymentCardMapper = paymentCardMapper;
    this.userRepository = userRepository;
    this.hashService = hashService;
    this.cacheService = cacheService;
  }

  @Override
  @Transactional
  public PaymentCardResponse create(CreatePaymentCardRequest createPaymentCardRequest) {
    Long userId = createPaymentCardRequest.userId();
    Optional<User> user = userRepository.findByIdForUpdate(userId);
    if (user.isEmpty()) {
      throw new NoDataException("User not found");
    }
    String number = createPaymentCardRequest.number();
    if (paymentCardRepository.existsByNumber(number)) {
      throw new ConflictException("Number already in use");
    }
    Long cardCount = paymentCardRepository.countByUserIdAndActiveIsTrue(userId);
    if (cardCount < MAX_CARDS_PER_USER_CONST) {
      PaymentCard paymentCard = paymentCardMapper.createPaymentCardRequestToEntity(createPaymentCardRequest);
      String hashedNumber = hashService.sha256(number);
      paymentCard.setNumber(hashedNumber);
      PaymentCard savedCard = paymentCardRepository.save(paymentCard);
      return paymentCardMapper.paymentCardToResponse(savedCard);
    } else {
      throw new ConflictException("User can have maximum 5 cards");
    }
  }

  @Override
  @Transactional
  public PaymentCardResponse update(Long id, UpdatePaymentCardRequest updatePaymentCardRequest) {
    Optional<PaymentCard> paymentCard = paymentCardRepository.findById(id);
    if (paymentCard.isEmpty()) {
      throw new NoDataException("Payment card not found");
    }

    PaymentCard paymentCardEntity = paymentCard.get();
    paymentCardMapper.updatePaymentCard(updatePaymentCardRequest, paymentCardEntity);
    PaymentCard updatedCard = paymentCardRepository.save(paymentCardEntity);
    Long userId = paymentCardEntity.getUser().getId();
     cacheService.evictUserCache(userId);
    return paymentCardMapper.paymentCardToResponse(updatedCard);
  }

  @Override
  public PaymentCardResponse findById(Long id) {
    Optional<PaymentCard> paymentCard = paymentCardRepository.findById(id);
    if (paymentCard.isPresent()) {
      PaymentCard paymentCardEntity = paymentCard.get();
      return paymentCardMapper.paymentCardToResponse(paymentCardEntity);
    } else {
      throw new NoDataException("Payment card not found");
    }
  }

  @Override
  @Transactional
  public void activate(Long id) {
    int countChangedRows = paymentCardRepository.activate(id);
    if (countChangedRows == 0) {
      throw new NoDataException("Payment card not found");
    }
    Long userId = paymentCardRepository.getUserIdById(id).orElseThrow(() -> new NoDataException("Card not found"));
    cacheService.evictUserCache(userId);
  }

  @Override
  @Transactional
  public void deactivate(Long id) {
    int countChangedRows = paymentCardRepository.deactivate(id);
    if (countChangedRows == 0) {
      throw new NoDataException("Payment card not found");
    }
    Long userId = paymentCardRepository.getUserIdById(id).orElseThrow(() -> new NoDataException("Card not found"));
    cacheService.evictUserCache(userId);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Long userId = paymentCardRepository.getUserIdById(id).orElseThrow(() -> new NoDataException("Card not found"));
    paymentCardRepository.deleteById(id);
    cacheService.evictUserCache(userId);
  }

  @Override
  public Page<PaymentCardResponse> findAllByOwnerNameAndSurname(FilterByOwnerNameAndSurnameRequest filterByOwnerNameAndSurnameRequest, Pageable pageable) {
    String name = filterByOwnerNameAndSurnameRequest.name();
    String surname = filterByOwnerNameAndSurnameRequest.surname();
    Specification<PaymentCard> specification = Specification.where(
            PaymentCardSpecification.filterByOwnerName(name)
                    .and(PaymentCardSpecification.filterByOwnerSurname(surname)));
    Page<PaymentCard> paymentCards = paymentCardRepository.findAll(specification, pageable);
    return paymentCards.map(paymentCardMapper::paymentCardToResponse);
  }

  @Override
  public List<PaymentCardResponse> findAllByUserId(Long userId) {
    List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserId(userId);
    return paymentCards.stream().map(paymentCardMapper::paymentCardToResponse).toList();
  }
}