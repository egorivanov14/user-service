package org.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.user_service.DTO.AddressCreateRequest;
import org.user_service.DTO.AddressResponse;
import org.user_service.entity.Address;
import org.user_service.entity.User;
import org.user_service.exeptions.ResourceNotFoundException;
import org.user_service.mappers.AddressMapper;
import org.user_service.repository.AddressRepository;
import org.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public AddressResponse create(Long userId, AddressCreateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Address address = addressMapper.toEntity(request, user);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getByUserId(Long userId){
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        else{
            return addressRepository.findByUserId(userId).stream().map(addressMapper::toResponse).toList();
        }
    }

    @Transactional
    public void deleteAddress(Long Id){
        if(!addressRepository.existsById(Id)){
            throw new ResourceNotFoundException("Address not found: " + Id);
        }
        else{
            addressRepository.deleteById(Id);
        }
    }

    @Transactional
    public void deleteAllByUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        else{
            addressRepository.deleteByUserId(userId);
        }
    }

}
