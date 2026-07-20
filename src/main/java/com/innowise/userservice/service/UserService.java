package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserResponse create(CreateUserRequest createUserRequest);

  UserResponse update(Long id, UpdateUserRequest updateUserRequest);

  UserResponse findById(Long id);

  void delete(Long id);

  void activate(Long id);

  void deactivate(Long id);

  Page<UserResponse> findAll(Pageable pageable);

  Page<UserResponse> findAllAndFilterByNameAndSurname(Pageable pageable, FilterByNameAndSurnameRequest filterByNameAndSurnameRequest);
}