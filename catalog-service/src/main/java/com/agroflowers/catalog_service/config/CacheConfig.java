package com.agroflowers.catalog_service.config;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.agroflowers.catalog_service.dto.FarmResponseDto;
import com.agroflowers.catalog_service.dto.FlowerResponseDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        RedisCacheConfiguration farmsConfig = withListSerializer(defaultConfig, FarmResponseDto.class);
        RedisCacheConfiguration farmByIdConfig = withSerializer(defaultConfig, FarmResponseDto.class);
        RedisCacheConfiguration flowersConfig = withListSerializer(defaultConfig, FlowerResponseDto.class);
        RedisCacheConfiguration flowerByIdConfig = withSerializer(defaultConfig, FlowerResponseDto.class);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(
                        "farms", farmsConfig,
                        "farmById", farmByIdConfig,
                        "flowers", flowersConfig,
                        "flowerById", flowerByIdConfig
                ))
                .build();
    }

    private <T> RedisCacheConfiguration withSerializer(RedisCacheConfiguration base, Class<T> type) {
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(type);
        return base.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private <T> RedisCacheConfiguration withListSerializer(RedisCacheConfiguration base, Class<T> elementType) {
        JavaType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, elementType);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(listType);
        return base.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
