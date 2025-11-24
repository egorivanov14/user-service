package org.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.user_service.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUsername(String username);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < ?1 OR rt.revoked = true")
    void deleteExpiredOrRevokedTokens(Instant now);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.username = ?1")
    void deleteByUsername(String username);
}