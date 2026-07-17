package com.innowise.userservice.specification;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

import static com.innowise.userservice.config.ConstantConfiguration.*;

public class PaymentCardSpecification {
  public static Specification<PaymentCard> filterByOwnerName(String ownerName) {
    return ((root, query, criteriaBuilder) ->
            ownerName == null ? null : criteriaBuilder.equal(root.get(USER_CONST).get(NAME_CONST), ownerName));
  }

  public static Specification<PaymentCard> filterByOwnerSurname(String ownerSurname) {
    return ((root, query, criteriaBuilder) ->
            ownerSurname == null ? null : criteriaBuilder.equal(root.get(USER_CONST).get(SURNAME_CONST), ownerSurname));
  }
}