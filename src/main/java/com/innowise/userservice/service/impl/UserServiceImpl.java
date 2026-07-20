package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.FilterByNameAndSurnameRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ConflictException;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PaymentCardService paymentCardService;
  private final PaymentCardRepository paymentCardRepository;

  public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PaymentCardService paymentCardService, PaymentCardRepository paymentCardRepository) {
    this.userMapper = userMapper;
    this.userRepository = userRepository;
    this.paymentCardService = paymentCardService;
    this.paymentCardRepository = paymentCardRepository;
  }

  @Override
  @Transactional
  public UserResponse create(CreateUserRequest createUserRequest) {
    String email = createUserRequest.email();
    if (userRepository.existsByEmail(email)) {
      throw new ConflictException("User with this email already exists");
    }
    User user = userMapper.createUserRequestToEntity(createUserRequest);
    User savedUser = userRepository.save(user);
    return userMapper.userToUserResponseEntity(savedUser);
  }

  @Override
  @Transactional
  @CachePut(value = "users", key = "#id")
  public UserResponse update(Long id, UpdateUserRequest updateUserRequest) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      throw new NoDataException("User not found");
    }
    String email = updateUserRequest.email();
    if (email != null && userRepository.existsByEmail(email)) {
      throw new ConflictException("Email already in use");
    }
    User user = optionalUser.get();
    userMapper.updateEntity(updateUserRequest, user);
    User savedUser = userRepository.save(user);
    return userMapper.userToUserResponseEntity(savedUser);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void delete(Long id) {
    userRepository.deleteById(id);
    List<PaymentCard> paymentCardList = paymentCardRepository.findAllByUserId(id);
    paymentCardList.forEach(paymentCard -> {
      Long paymentCardId = paymentCard.getId();
      paymentCardService.evictCache(paymentCardId);
    });
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void activate(Long id) {
    int countRows = userRepository.activate(id);
    if (countRows == 0) {
      throw new NoDataException("User not found");
    }
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void deactivate(Long id) {
    int countRows = userRepository.deactivate(id);
    if (countRows == 0) {
      throw new NoDataException("User not found");
    }
  }

  @Override
  @Cacheable(value = "users", key = "#id", sync = true)
  public UserResponse findById(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      return userMapper.userToUserResponseEntity(user);
    } else {
      throw new NoDataException("User not found");
    }
  }

  @Override
  public Page<UserResponse> findAll(Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    return users.map(userMapper::userToUserResponseEntity);
  }

  @Override
  public Page<UserResponse> findAllAndFilterByNameAndSurname(Pageable pageable, FilterByNameAndSurnameRequest
          filterByNameAndSurnameRequest) {
    String name = filterByNameAndSurnameRequest.name();
    String surname = filterByNameAndSurnameRequest.surname();
    Specification<User> nameAndSurnameSpecification = Specification.where(
            UserSpecification.filterByName(name).and(UserSpecification.filterBySurname(surname)));
    Page<User> users = userRepository.findAll(nameAndSurnameSpecification, pageable);
    return users.map(userMapper::userToUserResponseEntity);
  }
}