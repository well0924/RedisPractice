package com.example.redispractice.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@AllArgsConstructor
public class RedisConcurrencyRepository {
    private final RedisTemplate<String,String>redisTemplate;

    //Lettuce 를 활용해서 락을 획득
    public Boolean lock(Long key){
        //setnx 명령어를 사용해서 락을 획득
        //setnx : 키와 벨류를 Set 할 때 기존의 값이 없을 때만 set
        return redisTemplate.opsForValue().setIfAbsent(generateKey(key),"lock", Duration.ofMillis(30000));
    }
    
    //락 해제
    public Boolean unlock(Long key){
        return redisTemplate.delete(generateKey(key));
    }

    //redission을 활용한 동시성 제어


    private String generateKey(Long key){
        return key.toString();
    }
}
