package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @Modifying(clearAutomatically = true) //todo @Transactional in UserService
  @Query(value = "UPDATE users SET active = true WHERE id = :id", nativeQuery = true)
  int activate(@Param("id") Long id);

  @Modifying(clearAutomatically = true) //todo @Transactional in UserService
  @Query(value = "UPDATE users SET active = false WHERE id = :id", nativeQuery = true)
  int deactivate(@Param("id") Long id);
}