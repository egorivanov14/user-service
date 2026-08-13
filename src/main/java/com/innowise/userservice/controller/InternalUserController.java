package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/*
*  TEMPORARY CONTROLLER
*
* Controller just to allows internal requests from other services.
*
* */

@RestController
@RequestMapping("/api/internal/users")
public class InternalUserController {
  private final UserService userService;

  public InternalUserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest createUserRequest) {
    UserResponse userResponse = userService.create(createUserRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/get/{id}")
  public ResponseEntity<UserResponse> get(@PathVariable Long id) {
    UserResponse response = userService.findById(id);
    return ResponseEntity.ok(response);
  }
}