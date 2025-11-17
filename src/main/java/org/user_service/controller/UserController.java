package org.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.user_service.dto.AddressDto;
import org.user_service.dto.RoleDto;
import org.user_service.dto.UserDto;
import org.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User profile and address management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateCurrentUser(userDto));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user account")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        userService.deleteCurrentUser();
    }

    // Address endpoints
    @GetMapping("/me/addresses")
    @Operation(summary = "Get current user addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressDto>> getCurrentUserAddresses() {
        return ResponseEntity.ok(userService.getCurrentUserAddresses());
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Add address to current user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDto> addAddress(@RequestBody AddressDto addressDto) {
        return new ResponseEntity<>(userService.addAddress(addressDto), HttpStatus.CREATED);
    }

    @PutMapping("/me/addresses/{addressId}")
    @Operation(summary = "Update user address")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(userService.updateAddress(addressId, addressDto));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "Delete user address")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long addressId) {
        userService.deleteAddress(addressId);
    }

    // Admin endpoints
    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}/role")
    @Operation(summary = "Update user role (Admin only)")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRole(userId, role));
    }

    // New endpoint for creating roles
    @PostMapping("/roles")
    @Operation(summary = "Create new role (Admin only)")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@RequestParam String roleName) {
        return new ResponseEntity<>(userService.createRole(roleName), HttpStatus.CREATED);
    }
}