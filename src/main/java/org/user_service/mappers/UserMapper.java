package org.user_service.mappers;

import org.springframework.stereotype.Component;
import org.user_service.DTO.UserCreateRequest;
import org.user_service.DTO.UserResponse;
import org.user_service.entity.Role;
import org.user_service.entity.User;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));

        return response;
    }

    public User toEntity(UserCreateRequest request){
        User user = new User();

        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPasswordHash());
        user.setFullName(request.getFullName());

        return user;
    }

}
