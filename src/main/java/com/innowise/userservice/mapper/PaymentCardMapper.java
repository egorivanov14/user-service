package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "user.id", source = "userId")
  @Mapping(target = "number", ignore = true)
  PaymentCard createPaymentCardRequestToEntity(CreatePaymentCardRequest createPaymentCardRequest);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "number", ignore = true)
  PaymentCardResponse paymentCardToResponse(PaymentCard paymentCard);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "number", ignore = true)
  void updatePaymentCard(UpdatePaymentCardRequest updatePaymentCardRequest, @MappingTarget PaymentCard paymentCard);
}