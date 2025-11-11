package org.user_service.service;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user_service.DTO.RoleCreateRequest;
import org.user_service.DTO.RoleResponse;
import org.user_service.entity.Role;
import org.user_service.exeptions.ResourceNotFoundException;
import org.user_service.mappers.RoleMapper;
import org.user_service.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse create(RoleCreateRequest request){
        if(roleRepository.existsByName(request.getName())){
            throw new DuplicateRequestException("Role already exists: " + request.getName());
        }

        Role role = roleMapper.toEntity(request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(Long id, RoleCreateRequest request){
        Role role = findRoleById(id);

        if(!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())){
            throw new DuplicateRequestException("Role already exists: " + request.getName());
        }

        role.setName(request.getName());
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional(readOnly = true)
    public RoleResponse getById(Long id){
        return roleMapper.toResponse(findRoleById(id));
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAll(){
        return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
    }

    @Transactional
    public void delete(Long id){
        if(!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role not found: " + id);
        }

        roleRepository.deleteById(id);
    }




    private Role findRoleById(Long id){
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }
}
