package org.user_service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class UserCreateRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private String fullName;

    private Set<@NotNull Long> roleIds;
}
