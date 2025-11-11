package org.user_service.mappers;

import org.springframework.stereotype.Component;
import org.user_service.DTO.RoleCreateRequest;
import org.user_service.DTO.RoleResponse;
import org.user_service.entity.Role;

@Component
public class RoleMapper {

    public Role toEntity(RoleCreateRequest request){
        Role role = new Role();
        role.setName(request.getName());

        return role;
    }

    public RoleResponse toResponse(Role role){
        RoleResponse response = new RoleResponse();

        response.setId(role.getId());
        response.setName(role.getName());

        return response;
    }

}
