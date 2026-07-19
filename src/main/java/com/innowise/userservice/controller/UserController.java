package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.*;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest createUserRequest) {
    UserResponse userResponse = userService.create(createUserRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
    UserResponse userResponse = userService.update(id, updateUserRequest);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<UserResponse> get(@PathVariable Long id) {
    UserResponse response = userService.findById(id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/activate/{id}")
  public ResponseEntity<Void> activate(@PathVariable Long id) {
    userService.activate(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/deactivate/{id}")
  public ResponseEntity<Void> deactivate(@PathVariable Long id) {
    userService.deactivate(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping()
  public Page<UserResponse> getAll(Pageable pageable) {
    return userService.findAll(pageable);
  }

  @PostMapping("/filter")
  public Page<UserResponse> getFiltered(@Valid @RequestBody FilterByNameAndSurnameRequest filterByNameAndSurnameRequest, Pageable pageable) {
    return userService.findAllAndFilterByNameAndSurname(pageable, filterByNameAndSurnameRequest);
  }
}