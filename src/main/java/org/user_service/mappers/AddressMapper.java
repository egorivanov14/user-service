package org.user_service.mappers;

import org.springframework.stereotype.Component;
import org.user_service.DTO.AddressCreateRequest;
import org.user_service.DTO.AddressResponse;
import org.user_service.entity.Address;
import org.user_service.entity.User;

@Component
public class AddressMapper {

    public AddressResponse toResponse(Address address){
        AddressResponse response = new AddressResponse();

        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setZip(address.getZip());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setUserId(address.getUser().getId());

        return response;
    }

    public Address toEntity(AddressCreateRequest request, User user){
        Address address = new Address();

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setZip(request.getZip());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setUser(user);

        return address;
    }

}
