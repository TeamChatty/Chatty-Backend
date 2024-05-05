package com.chatty.repository.auth;

import com.chatty.utils.redis.RedisUtils;
import java.util.List;
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
    private static final int EXPIRED_TIME = 5;
    private static final String LIMIT= "limit";
    private static final String LIMIT_FIRST_TIME = "1";

    public void save(String key, String authNumber) {
        ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
        value.set(key, authNumber, EXPIRED_TIME, TimeUnit.MINUTES);
    }

    public void saveLimitNumber(String key) {
        redisTemplateAuthNumber.opsForValue().set(key + LIMIT, LIMIT_FIRST_TIME, RedisUtils.getUntilMidnight(), TimeUnit.SECONDS);
    }

    public String findAuthNumber(String key) {
        return redisTemplateAuthNumber.opsForValue().get(key);
    }

    public void updateAuthLimitNumber(String key, String limitValue) {
        redisTemplateAuthNumber.opsForValue().set(makeKey(key), String.valueOf(Integer.valueOf(limitValue) + 1));
    }

    public void resetData() {
        redisTemplateAuthNumber.delete(redisTemplateAuthNumber.keys("*")); // 모든 데이터 삭제
    }

    public String findAuthLimitNumber(String key) {
        return redisTemplateAuthNumber.opsForValue().get(makeKey(key));
    }

    private String makeKey(final String key) {
        return key + LIMIT;
    }
}
