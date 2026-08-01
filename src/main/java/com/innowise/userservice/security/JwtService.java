package com.innowise.userservice.security;

import com.innowise.userservice.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static com.innowise.userservice.config.ConstantConfiguration.ROLE_CONST;

@Service
public class JwtService {

  private final String secretKey;

  public JwtService(@Value("${jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  public Claims validateAndGetClaims(String token) {
    SecretKey key = getSignInKey();
    try {
      return Jwts.parser()
              .verifyWith(key)
              .build()
              .parseSignedClaims(token)
              .getPayload();
    } catch (JwtException e) {
      throw new AuthenticationException(e.getMessage());
    }
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }
}