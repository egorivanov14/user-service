package org.user_service.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> roleIds;

}

