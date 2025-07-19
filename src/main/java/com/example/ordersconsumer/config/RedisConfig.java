package com.example.ordersconsumer.config;

import com.example.ordersconsumer.model.kafka.FailedOrderDTO;
import com.example.ordersconsumer.model.kafka.OrderInputDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, FailedOrderDTO> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<FailedOrderDTO> serializer = new Jackson2JsonRedisSerializer<>(FailedOrderDTO.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, FailedOrderDTO> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, FailedOrderDTO> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public RedisTemplate<String, FailedOrderDTO> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, FailedOrderDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<FailedOrderDTO> serializer = new Jackson2JsonRedisSerializer<>(FailedOrderDTO.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}