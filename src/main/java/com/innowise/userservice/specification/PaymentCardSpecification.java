package com.innowise.userservice.specification;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

import static com.innowise.userservice.config.ConstantConfiguration.*;

public class PaymentCardSpecification {
  public static Specification<PaymentCard> filterByUserName(String userName) {
    return ((root, query, criteriaBuilder) ->
            userName == null ? null : criteriaBuilder.equal(root.get(USER_CONST).get(NAME_CONST), userName));
  }

  public static Specification<PaymentCard> filterByUserSurname(String userSurname) {
    return ((root, query, criteriaBuilder) ->
            userSurname == null ? null : criteriaBuilder.equal(root.get(USER_CONST).get(SURNAME_CONST), userSurname));
  }
}