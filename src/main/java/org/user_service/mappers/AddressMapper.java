package org.user_service.mappers;

import org.mapstruct.Mapper;
import org.user_service.dto.AddressDto;
import org.user_service.entity.Address;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    // Уберите @Mapping(target = "user", ignore = true) - это не нужно
    Address toEntity(AddressDto addressDto);

    AddressDto toDto(Address address);

    List<AddressDto> toDtoList(List<Address> addresses);
}