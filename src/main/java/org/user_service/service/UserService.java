package org.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user_service.dto.*;
import org.user_service.entity.Address;
import org.user_service.entity.RefreshToken;
import org.user_service.entity.Role;
import org.user_service.entity.User;
import org.user_service.exeptions.DuplicateResourceException;
import org.user_service.exeptions.ResourceNotFoundException;
import org.user_service.exeptions.UnauthorizedException;
import org.user_service.mappers.UserMapper;
import org.user_service.repository.AddressRepository;
import org.user_service.repository.RefreshTokenRepository;
import org.user_service.repository.RoleRepository;
import org.user_service.repository.UserRepository;
import org.user_service.securiry.JwtTokenProvider;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.registerRequestToUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role 'USER' not found. Please create it first."));
        user.setRoles(new HashSet<>(Set.of(userRole)));

        User savedUser = userRepository.save(user);

        // Создаем Authentication объект вручную без вызова authenticationManager
        // Это позволяет сгенерировать токены сразу после регистрации
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser, null, savedUser.getAuthorities()
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        saveRefreshToken(savedUser.getEmail(), refreshToken);

        UserDto userDto = userMapper.toDto(savedUser);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        saveRefreshToken(user.getEmail(), refreshToken);

        UserDto userDto = userMapper.toDto(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    private void saveRefreshToken(String username, String token) {
        refreshTokenRepository.deleteByUsername(username);

        RefreshToken refreshTokenEntity = new RefreshToken(
                token,
                username,
                Instant.now().plusMillis(tokenProvider.getRefreshTokenExpiration())
        );
        refreshTokenRepository.save(refreshTokenEntity);
    }

    // Остальные методы без изменений...
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateCurrentUser(UserDto userDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.deleteByUsername(email);
        userRepository.delete(user);
    }

    // Address operations
    @Transactional(readOnly = true)
    public List<AddressDto> getCurrentUserAddresses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getAddresses().stream()
                .map(address -> {
                    AddressDto dto = new AddressDto();
                    dto.setId(address.getId());
                    dto.setStreet(address.getStreet());
                    dto.setCity(address.getCity());
                    dto.setZip(address.getZip());
                    dto.setState(address.getState());
                    dto.setCountry(address.getCountry());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(AddressDto addressDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setZip(addressDto.getZip());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        addressDto.setId(savedAddress.getId());
        return addressDto;
    }

    @Transactional
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Address does not belong to current user");
        }

        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getZip() != null) address.setZip(addressDto.getZip());
        if (addressDto.getState() != null) address.setState(addressDto.getState());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());

        Address updatedAddress = addressRepository.save(address);
        addressDto.setId(updatedAddress.getId());
        return addressDto;
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Address does not belong to current user");
        }

        addressRepository.delete(address);
    }

    // Admin operations
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public RoleDto createRole(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            throw new DuplicateResourceException("Role already exists: " + roleName);
        }

        Role role = new Role();
        role.setName(roleName);
        Role savedRole = roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setId(savedRole.getId());
        dto.setName(savedRole.getName());
        return dto;
    }

    // Token refresh logic
    @Transactional
    public TokenRefreshResponse refreshAccessToken(String refreshTokenValue) {
        if (!tokenProvider.validateRefreshToken(refreshTokenValue)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token has been revoked or expired");
        }

        String username = tokenProvider.getUsernameFromToken(refreshTokenValue);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        saveRefreshToken(username, newRefreshToken);

        return new TokenRefreshResponse(newAccessToken, newRefreshToken, tokenProvider.getAccessTokenExpiration());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}