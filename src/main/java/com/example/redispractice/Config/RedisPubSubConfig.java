package com.example.redispractice.Config;

import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@NoArgsConstructor
public class RedisPubSubConfig {

    /**
     * pub/sub 설정
     **/
    @Bean
    @Primary
    public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory redisConnectionFactory){
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }
}
