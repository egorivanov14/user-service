package com.innowise.userservice.specification;

import com.innowise.userservice.entity.User;
import org.springframework.data.jpa.domain.Specification;

import static com.innowise.userservice.config.ConstantConfiguration.NAME_CONST;
import static com.innowise.userservice.config.ConstantConfiguration.SURNAME_CONST;

public class UserSpecification {
  public static Specification<User> filterByName(String name) {
    return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.equal(root.get(NAME_CONST), name);
  }

  public static Specification<User> filterBySurname(String surname) {
    return (root, query, criteriaBuilder) ->
            surname == null ? null : criteriaBuilder.equal(root.get(SURNAME_CONST), surname);
  }
}