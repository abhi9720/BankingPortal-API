package com.webapp.bankingportal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration for the banking portal.
 *
 * <p>Configures a {@link RedisTemplate} that uses string serialization for keys and
 * JSON serialization (via Jackson) for values. The Jackson {@link com.fasterxml.jackson.databind.ObjectMapper}
 * is configured with {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} to properly
 * serialize/deserialize Java 8 date/time types.</p>
 */
@Configuration
public class RedisConfig {

    /**
     * Builds a {@link RedisTemplate} configured for {@code String} keys and
     * JSON-serialized {@code Object} values.
     *
     * @param connectionFactory the Redis connection factory provided by Spring Boot auto-configuration
     * @return the configured {@link RedisTemplate} bean
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
