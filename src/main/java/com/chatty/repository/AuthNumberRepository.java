package com.chatty.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthNumberRepository {

    private static final int VALIDITY_TIME = 1000 * 60 * 3;

    private final RedisTemplate<String, String> redisTemplateAuthNumber;

    public void save(String key, String authNumber) {
        try {
            ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
            value.set(key,authNumber,VALIDITY_TIME);
        }catch(Exception e) {
            log.error("[RedistTokenService/getRefreshTokenByUuid] 데이터 저장 실패");
        }
    }

    public String findAuthNumber(String key) {
        try {
            ValueOperations<String, String> value = redisTemplateAuthNumber.opsForValue();
            return value.get(key);
        }catch(Exception e) {
            log.error("[RedistTokenService/getRefreshTokenByUuid] 일치하는 refresh 토큰이 존재하지 않습니다.");
            return null;
        }
    }
}
