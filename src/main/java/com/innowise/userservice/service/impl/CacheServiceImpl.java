package com.innowise.userservice.service.impl;

import com.innowise.userservice.service.CacheService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {
  @Override
  @CacheEvict(value = "users", key = "#userId")
  public void evictUserCache(Long userId) {
  }
}