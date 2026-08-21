package com.innowise.userservice.unit.service;

import com.innowise.userservice.client.GrpcClientService;
import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.FilterByNameAndSurnameRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ConflictException;
import com.innowise.userservice.exception.NoDataException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.innowise.userservice.TestConstantConfiguration.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserMapper userMapper;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PaymentCardService paymentCardService;
  @Mock
  private PaymentCardRepository paymentCardRepository;
  @Mock
  private PaymentCardMapper paymentCardMapper;
  @Mock
  private GrpcClientService grpcClientService;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void create_correctRequest_shouldCreateUser() {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);
    User user = new User();
    UserResponse expectedResponse = new UserResponse(ID, NAME, SURNAME, BIRTH_DATE, EMAIL, ACTIVE, CREATED_AT, UPDATED_AT, EMPTY_CARD_RESPONSES_LIST);

    when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
    when(userMapper.createUserRequestToEntity(createUserRequest)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST)).thenReturn(expectedResponse);

    UserResponse actualResponse = userService.create(createUserRequest);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void create_incorrectRequest_existedEmail_shouldThrowConflictException() {
    CreateUserRequest createUserRequest = new CreateUserRequest(NAME, SURNAME, BIRTH_DATE, EMAIL);

    when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

    assertThrows(ConflictException.class, () -> {
      userService.create(createUserRequest);
    });

    verify(userRepository, never()).save(any());
  }

  @Test
  void update_shouldUpdateEmail() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, UPDATE_EMAIL);
    User user = new User();
    UserResponse expectedResponse = new UserResponse(ID, NAME, SURNAME, BIRTH_DATE, UPDATE_EMAIL, ACTIVE, CREATED_AT, UPDATED_AT, EMPTY_CARD_RESPONSES_LIST);

    when(userRepository.findById(ID)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(UPDATE_EMAIL)).thenReturn(false);
    doNothing().when(userMapper).updateEntity(updateUserRequest, user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST)).thenReturn(expectedResponse);

    UserResponse actualResponse = userService.update(ID, updateUserRequest);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void update_shouldUpdateName() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest(UPDATE_NAME, null, null, null);
    User user = new User();
    UserResponse expectedResponse = new UserResponse(ID, UPDATE_NAME, SURNAME, BIRTH_DATE, EMAIL, ACTIVE, CREATED_AT, UPDATED_AT, EMPTY_CARD_RESPONSES_LIST);

    when(userRepository.findById(ID)).thenReturn(Optional.of(user));
    doNothing().when(userMapper).updateEntity(updateUserRequest, user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST)).thenReturn(expectedResponse);

    UserResponse actualResponse = userService.update(ID, updateUserRequest);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void update_userNotFound_shouldThrowNoDataException() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, UPDATE_EMAIL);

    when(userRepository.findById(ID)).thenReturn(Optional.empty());

    assertThrows(NoDataException.class, () -> {
      userService.update(ID, updateUserRequest);
    });
    verify(userRepository, never()).save(any());
  }

  @Test
  void update_incorrectRequest_existedEmail_shouldThrowConflictException() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, UPDATE_EMAIL);
    User user = new User();

    when(userRepository.findById(ID)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(UPDATE_EMAIL)).thenReturn(true);

    assertThrows(ConflictException.class, () -> {
      userService.update(ID, updateUserRequest);
    });
    verify(userRepository, never()).save(any());
  }

  @Test
  void findById_shouldFindUser() {
    User user = new User();
    UserResponse expectedResponse = new UserResponse(ID, NAME, SURNAME, BIRTH_DATE, EMAIL, ACTIVE, CREATED_AT, UPDATED_AT, EMPTY_CARD_RESPONSES_LIST);

    when(userRepository.findById(ID)).thenReturn(Optional.of(user));
    when(paymentCardRepository.findAllByUserId(ID)).thenReturn(EMPTY_PAYMENT_CARD_LIST);
    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST)).thenReturn(expectedResponse);

    UserResponse actualResponse = userService.findById(ID);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void findById_shouldThrowNoDataException() {
    when(userRepository.findById(ID)).thenReturn(Optional.empty());

    assertThrows(NoDataException.class, () -> {
      userService.findById(ID);
    });
    verify(userRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteUser() {
    doNothing().when(userRepository).deleteById(ID);
    doNothing().when(grpcClientService).deleteUserCredentials(ID);

    userService.delete(ID);
    verify(userRepository).deleteById(ID);
  }

  @Test
  void activate_shouldActivateUser() {
    Long id = ID;

    when(userRepository.activate(id)).thenReturn(ACTIVATED_USER_ROWS);

    userService.activate(id);
    verify(userRepository).activate(id);
  }

  @Test
  void activate_shouldThrowNoDataException() {
    Long id = ID;

    when(userRepository.activate(id)).thenReturn(ZERO_ROWS);

    assertThrows(NoDataException.class, () -> {
      userService.activate(id);
    });
  }

  @Test
  void deactivate_shouldDeactivateUser() {
    Long id = ID;

    when(userRepository.deactivate(id)).thenReturn(DEACTIVATED_USER_ROWS);

    userService.deactivate(id);
    verify(userRepository).deactivate(id);
  }

  @Test
  void deactivate_shouldThrowNoDataException() {
    Long id = ID;
    when(userRepository.deactivate(id)).thenReturn(ZERO_ROWS);
    assertThrows(NoDataException.class, () -> {
      userService.deactivate(id);
    });
  }

  @Test
  void findAll_shouldFindAllUsers() {
    Pageable pageable = PageRequest.of(0, 10);

    User user = new User();
    UserResponse response = new UserResponse(
            ID,
            NAME,
            SURNAME,
            BIRTH_DATE,
            EMAIL,
            ACTIVE,
            CREATED_AT,
            UPDATED_AT,
            EMPTY_CARD_RESPONSES_LIST
    );

    Page<User> users = new PageImpl<>(List.of(user));

    when(userRepository.findAll(pageable)).thenReturn(users);
    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST)).thenReturn(response);

    Page<UserResponse> result = userService.findAll(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(response, result.getContent().getFirst());

    verify(userRepository).findAll(pageable);
  }

  @Test
  void findAllAndFilterByNameAndSurname_shouldReturnUsers() {
    Pageable pageable = PageRequest.of(0, 10);

    FilterByNameAndSurnameRequest request =
            new FilterByNameAndSurnameRequest(NAME, SURNAME);

    User user = new User();

    UserResponse response = new UserResponse(
            ID,
            NAME,
            SURNAME,
            BIRTH_DATE,
            EMAIL,
            ACTIVE,
            CREATED_AT,
            UPDATED_AT,
            EMPTY_CARD_RESPONSES_LIST
    );

    Page<User> users = new PageImpl<>(List.of(user));

    when(userRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(users);

    when(userMapper.userToUserResponseEntity(user, EMPTY_CARD_RESPONSES_LIST))
            .thenReturn(response);

    Page<UserResponse> result =
            userService.findAllAndFilterByNameAndSurname(pageable, request);

    assertEquals(1, result.getTotalElements());
    assertEquals(response, result.getContent().getFirst());

    verify(userRepository).findAll(any(Specification.class), eq(pageable));
  }
}