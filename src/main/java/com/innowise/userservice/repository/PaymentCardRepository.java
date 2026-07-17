package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  List<PaymentCard> findAllByUserId(@Param("user_id") Long userId);

  @Modifying(clearAutomatically = true) //todo @Transaction in PaymentCardService
  @Query(value = "UPDATE payment_cards SET active = true, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int activate(@Param("id") Long id);

  @Modifying(clearAutomatically = true) //todo @Transaction in PaymentCardService
  @Query(value = "UPDATE payment_cards SET active = false, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int deactivate(@Param("id") Long id);

  Long countByUserId(Long user_id);
}