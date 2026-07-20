package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.user.CreateUserRequest;
import com.innowise.userservice.dto.user.UpdateUserRequest;
import com.innowise.userservice.dto.user.UserResponse;
import com.innowise.userservice.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "paymentCards", ignore = true)
  User createUserRequestToEntity(CreateUserRequest createUserRequest);

  UserResponse userToUserResponseEntity(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "paymentCards", ignore = true)
  void updateEntity(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}