package com.chatty.repository.auth;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthNumberRepository {

    private final RedisTemplate<String, String> redisTemplateAuthNumber;
    private static final long VALIDITY_NUMBER = 5;

    public void save(String key, String authNumber) {
        ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
        value.set(key, authNumber, VALIDITY_NUMBER, TimeUnit.MINUTES);
    }

    public String findAuthNumber(String key) {
        ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
        return value.get(key);
    }

    public void delete(String key){
        redisTemplateAuthNumber.delete(key);
    }
}
