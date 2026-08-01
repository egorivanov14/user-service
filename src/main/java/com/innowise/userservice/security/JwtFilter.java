package com.innowise.userservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.innowise.userservice.config.ConstantConfiguration.ROLE_CONST;

@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      Claims claims = jwtService.validateAndGetClaims(token);
      String userIdString = claims.getSubject();
      Long userId = Long.parseLong(userIdString);
      String role = claims.get(ROLE_CONST, String.class);

      if (role != null) {
        SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(role);
        List<SimpleGrantedAuthority> authorities = List.of(roleAuthority);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }
}