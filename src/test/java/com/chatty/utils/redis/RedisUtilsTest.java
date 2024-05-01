package com.chatty.utils.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RedisUtilsTest {

    @Test
    @DisplayName("자정까지 시간 추출하기 테스트")
    void getUntilMidnight() throws Exception{
        //given
        long time = RedisUtils.getUntilMidnight();
        //when

        //then
        System.out.println(time);
    }
}