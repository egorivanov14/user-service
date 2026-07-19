package com.innowise.userservice.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class CacheConfig {

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    // разрешаем полиморфную десериализацию только для своих DTO и стандартных коллекций —
    // не для всего подряд, это открытая дверь для deserialization-атак
    PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.innowise.userservice.dto")
            .allowIfSubType("java.util")
            .build();

    RedisSerializationContext.SerializationPair<Object> jsonSerializer =
            RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer.builder()
                            .enableDefaultTyping(typeValidator)
                            .build());

    return builder -> builder
            .transactionAware()
            .cacheDefaults(
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(10))
                            .disableCachingNullValues()
                            .serializeValuesWith(jsonSerializer));
  }
}