package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdForUpdate(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE users SET active = true, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int activate(@Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Query(value = "UPDATE users SET active = false, updated_at = NOW() WHERE id = :id", nativeQuery = true)
  int deactivate(@Param("id") Long id);

  boolean existsByEmail(String email);
}