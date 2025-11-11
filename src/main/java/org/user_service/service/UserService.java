package org.user_service.service;

import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.user_service.DTO.UserCreateRequest;
import org.user_service.DTO.UserResponse;
import org.user_service.entity.Role;
import org.user_service.entity.User;
import org.user_service.exeptions.ResourceNotFoundException;
import org.user_service.mappers.UserMapper;
import org.user_service.repository.AddressRepository;
import org.user_service.repository.RoleRepository;
import org.user_service.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;


    @Transactional
    public UserResponse create(UserCreateRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateRequestException("Email already exists: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);

        if(request.getRoleIds() != null && !request.getRoleIds().isEmpty()){
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));

            user.setRoles(roles);
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id){
        return userMapper.toResponse(findUserById(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll(Pageable pageable){
        return userRepository.findAll(pageable).stream().map(userMapper::toResponse).toList();
    }

    @Transactional
    public UserResponse update(Long id, UserCreateRequest request){
        User user = findUserById(id);

        if(!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateRequestException("Email already exists: " + request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(request.getPasswordHash());

        if(request.getRoleIds() != null){
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));

            if(roles.size() != request.getRoleIds().size()){
                throw new ResourceNotFoundException("Some roles not found");
            }

            user.setRoles(roles);
        }

        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id){
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User not found: " + id);
        }

        addressRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }


    private User findUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
