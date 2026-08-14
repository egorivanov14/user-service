package com.innowise.userservice.service.impl;

import com.innowise.userservice.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {
  private static Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
  @Override
  @CacheEvict(value = "users", key = "#userId")
  public void evictUserCache(Long userId) {
    logger.debug("evictUserCache called");
  }
}