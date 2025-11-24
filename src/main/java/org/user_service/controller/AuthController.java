package org.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.user_service.dto.*;
import org.user_service.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user and automatically login")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        // После регистрации пользователь сразу получает токены
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshAccessToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-Refresh-Token") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        userService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }
}