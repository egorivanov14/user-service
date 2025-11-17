package org.user_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<AddressDto> addresses;
    private Set<String> roles;
}