package org.user_service.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String zip;
    private String state;
    private String country;
}
