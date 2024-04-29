package com.chatty.repository.token;

import static java.util.concurrent.TimeUnit.SECONDS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    @Value("${jwt-refresh-token-expiration-time}")
    private String validTime;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String deviceId, String refreshToken){
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        log.info("refresh 유효 시간 : {}", Long.parseLong(validTime) / 1000);
        value.set(deviceId,refreshToken, Long.parseLong(validTime)/1000, SECONDS);
    }

    public String findRefreshTokenByDeviceId(String deviceId){
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        return value.get(deviceId);
    }

    public void delete(String uuid){
        redisTemplate.delete(uuid);
    }
}
