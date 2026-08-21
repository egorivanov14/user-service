package com.innowise.userservice.service;

public interface CacheService {
  void evictUserCache(Long userId);
}