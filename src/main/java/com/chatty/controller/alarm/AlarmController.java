package com.chatty.controller.alarm;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.alarm.request.AlarmCreateRequest;
import com.chatty.dto.alarm.response.AlarmListResponse;
import com.chatty.dto.alarm.response.AlarmResponse;
import com.chatty.service.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/v1/alarm")
    public ApiResponse<AlarmResponse> createAlarm(@RequestBody AlarmCreateRequest request) {
        return ApiResponse.ok(alarmService.createAlarm(request));
    }

    @GetMapping("/v1/alarms")
    public ApiResponse<List<AlarmListResponse>> getAllAlarms(@RequestParam Long lastAlarmId,
                                                             @RequestParam int size,
                                                             Authentication authentication) {
        return ApiResponse.ok(alarmService.getAlarmListPages(lastAlarmId, size, authentication.getName()));
    }
}
