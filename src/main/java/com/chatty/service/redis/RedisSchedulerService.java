package com.chatty.service.redis;

import com.chatty.repository.auth.AuthNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSchedulerService {

    private final AuthNumberRepository authNumberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetData() { // 초기화
        System.out.println("redis 데이터가 추가됩니다.");
        authNumberRepository.resetData();
    }
}
