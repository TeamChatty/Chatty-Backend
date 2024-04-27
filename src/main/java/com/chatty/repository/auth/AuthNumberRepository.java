package com.chatty.repository.auth;

import com.chatty.utils.redis.RedisUtils;
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

    public void save(String key, String authNumber) {
        ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
        value.set(key, authNumber, RedisUtils.getUntilMidnight(), TimeUnit.SECONDS);
    }

    public String findAuthNumber(String key) {
        ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
        return value.get(key);
    }
}
