package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  List<PaymentCard> findAllByUserId(@Param("user_id") Long userId);

  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE payment_cards SET active = true, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int activate(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE payment_cards SET active = false, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int deactivate(@Param("id") Long id);

  Long countByUserIdAndActiveIsTrue(Long userId);

  boolean existsByNumber(String number);

  Optional<PaymentCard> findByNumber(String number);

  List<PaymentCard> findAllByUserIdIn(List<Long> userIds);

  List<PaymentCard> id(Long id);

  @Query(value = "SELECT user_id FROM payment_cards WHERE id = :id", nativeQuery = true)
  Optional<Long> getUserIdById(@Param("id") Long id);
}