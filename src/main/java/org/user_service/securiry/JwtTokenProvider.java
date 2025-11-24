package org.user_service.securiry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtTokenProvider {

    private String secret;
    private AccessToken accessToken = new AccessToken();
    private RefreshToken refreshToken = new RefreshToken();
    private SecretKey key;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class AccessToken {
        private long expiration;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }

    public static class RefreshToken {
        private long expiration;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accessToken.getExpiration(), "ACCESS");
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshToken.getExpiration(), "REFRESH");
    }

    private String generateToken(Authentication authentication, long expirationTime, String tokenType) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + expirationTime);

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("type", tokenType)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "ACCESS");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "REFRESH");
    }

    private boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenType = claims.get("type", String.class);
            return expectedType.equals(tokenType) && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpiration() {
        return accessToken.getExpiration();
    }

    public long getRefreshTokenExpiration() {
        return refreshToken.getExpiration();
    }
}