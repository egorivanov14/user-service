package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.card.CreatePaymentCardRequest;
import com.innowise.userservice.dto.card.PaymentCardResponse;
import com.innowise.userservice.dto.card.UpdatePaymentCardRequest;
import com.innowise.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "user.id", source = "userId")
  PaymentCard createPaymentCardRequestToEntity(CreatePaymentCardRequest createPaymentCardRequest);

  @Mapping(target = "userId", source = "user.id")
  PaymentCardResponse paymentCardToResponse(PaymentCard paymentCard);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updatePaymentCard(UpdatePaymentCardRequest updatePaymentCardRequest, @MappingTarget PaymentCard paymentCard);
}