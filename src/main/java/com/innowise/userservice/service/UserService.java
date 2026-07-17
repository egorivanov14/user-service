package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.FilterByNameAndSurnameRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserService {

  UserResponse create(CreateUserRequest createUserRequest);

  UserResponse update(Long id, UpdateUserRequest updateUserRequest);

  UserResponse findById(Long id);

  void delete(Long id);

  void activate(Long id);

  void deactivate(Long id);

  Page<UserResponse> findAll(PageRequest pageRequest);

  Page<UserResponse> findAllAndFilterByNameAndSurname(PageRequest pageRequest, FilterByNameAndSurnameRequest filterByNameAndSurnameRequest);
}