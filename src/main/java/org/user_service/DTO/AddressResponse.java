package org.user_service.DTO;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String zip;
    private String state;
    private String country;
    private Long userId;
}
