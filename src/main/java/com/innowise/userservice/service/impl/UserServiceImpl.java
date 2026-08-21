package com.innowise.userservice.service.impl;

import com.innowise.userservice.client.GrpcClientService;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.FilterByNameAndSurnameRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.AuthenticationServiceException;
import com.innowise.userservice.exception.ConflictException;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecification;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PaymentCardRepository paymentCardRepository;
  private final PaymentCardMapper paymentCardMapper;
  private final GrpcClientService grpcClientService;

  public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PaymentCardRepository paymentCardRepository, PaymentCardMapper paymentCardMapper, GrpcClientService grpcClientService) {
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.paymentCardRepository = paymentCardRepository;
    this.paymentCardMapper = paymentCardMapper;
    this.grpcClientService = grpcClientService;
  }

  @Override
  @Transactional
  public UserResponse create(CreateUserRequest createUserRequest) {
    logger.debug("create() called");
    String email = createUserRequest.email();
    if (userRepository.existsByEmail(email)) {
      logger.error("User with email {} already exists", email);
      throw new ConflictException("User with this email already exists");
    }
    User user = userMapper.createUserRequestToEntity(createUserRequest);
    User savedUser = userRepository.save(user);
    logger.info("User created: userId = {}", savedUser.getId());
    return userMapper.userToUserResponseEntity(savedUser, List.of());
  }

  @Override
  @Transactional
  @CachePut(value = "users", key = "#id")
  public UserResponse update(Long id, UpdateUserRequest updateUserRequest) {
    logger.debug("update() called: userId = {}", id);
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      logger.error("User with id {} not found", id);
      throw new NoDataException("User not found");
    }
    String email = updateUserRequest.email();
    if (email != null && userRepository.existsByEmail(email)) {
      logger.error("User with email {} already exists", email);
      throw new ConflictException("Email already in use");
    }
    User user = optionalUser.get();
    userMapper.updateEntity(updateUserRequest, user);
    User savedUser = userRepository.save(user);
    List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserId(id);
    List<PaymentCardResponse> paymentCardsResponse = paymentCards.stream().map(paymentCardMapper::paymentCardToResponse).toList();
    logger.info("Users updated: userId = {}", savedUser.getId());
    return userMapper.userToUserResponseEntity(savedUser, paymentCardsResponse);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void delete(Long id) {
    logger.debug("delete() called: userId = {}", id);
    userRepository.deleteById(id);
    try {
      grpcClientService.deleteUserCredentials(id);
    } catch (StatusRuntimeException e) {
      throw new AuthenticationServiceException(e.getMessage());
    }
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void activate(Long id) {
    logger.debug("activate() called: userId = {}", id);
    int countRows = userRepository.activate(id);
    if (countRows == 0) {
      throw new NoDataException("User not found");
    }
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void deactivate(Long id) {
    logger.debug("deactivate() called: userId = {}", id);
    int countRows = userRepository.deactivate(id);
    if (countRows == 0) {
      throw new NoDataException("User not found");
    }
  }

  @Override
  @Cacheable(value = "users", key = "#id", sync = true)
  public UserResponse findById(Long id) {
    logger.debug("findById() called: userId = {}", id);
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserId(id);
      List<PaymentCardResponse> paymentCardsResponse = paymentCards.stream().map(paymentCardMapper::paymentCardToResponse).toList();
      return userMapper.userToUserResponseEntity(user, paymentCardsResponse);
    } else {
      throw new NoDataException("User not found");
    }
  }

  @Override
  public Page<UserResponse> findAll(Pageable pageable) {
    logger.debug("findAll() called");
    Page<User> users = userRepository.findAll(pageable);
    return getUserResponsesPageFromUsersPage(users);
  }

  @Override
  public Page<UserResponse> findAllAndFilterByNameAndSurname(Pageable pageable, FilterByNameAndSurnameRequest
          filterByNameAndSurnameRequest) {
    logger.debug("findAllAndFilterByNameAndSurname() called");
    String name = filterByNameAndSurnameRequest.name();
    String surname = filterByNameAndSurnameRequest.surname();
    Specification<User> nameAndSurnameSpecification = Specification.where(
            UserSpecification.filterByName(name).and(UserSpecification.filterBySurname(surname)));
    Page<User> users = userRepository.findAll(nameAndSurnameSpecification, pageable);
    return getUserResponsesPageFromUsersPage(users);
  }

  private Page<UserResponse> getUserResponsesPageFromUsersPage(Page<User> users) {
    List<Long> userIds = users.stream()
            .map(User::getId)
            .toList();

    List<PaymentCard> paymentCards = paymentCardRepository.findAllByUserIdIn(userIds);
    Map<Long, List<PaymentCardResponse>> paymentCardsByUserId =
            paymentCards.stream().collect(Collectors.groupingBy(
                    paymentCard -> paymentCard.getUser().getId(),
                    Collectors.mapping(
                            paymentCardMapper::paymentCardToResponse,
                            Collectors.toList()
                    )
            ));

    return users.map(user ->
            userMapper.userToUserResponseEntity(
                    user,
                    paymentCardsByUserId.getOrDefault(user.getId(), List.of())
            )
    );
  }
}