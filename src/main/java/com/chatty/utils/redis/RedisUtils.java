package com.chatty.utils.redis;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RedisUtils {

    public static long getUntilMidnight() { // 현재 시간과 자정까지의 gap을 return 해주는 함수
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS); // 하루 더하고, 나머지 절삭
        return ChronoUnit.SECONDS.between(now,midnight);
    }
}
