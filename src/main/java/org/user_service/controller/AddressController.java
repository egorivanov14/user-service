package org.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.user_service.DTO.AddressCreateRequest;
import org.user_service.DTO.AddressResponse;
import org.user_service.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/api/addresses/{userId}")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> create(@PathVariable Long userId, @Valid @RequestBody AddressCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> get(@PathVariable Long userId){
        return ResponseEntity.ok(addressService.getByUserId(userId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteByAddressId(@PathVariable("addressId") Long addressId){

        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteByUserId(@PathVariable Long userId){

        addressService.deleteAllByUser(userId);
        return ResponseEntity.noContent().build();
    }


}
