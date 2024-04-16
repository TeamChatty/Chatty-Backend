package com.chatty.controller.notification;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationReceiveController {

    private final NotificationReceiveService notificationReceiveService;

    @GetMapping("/v1/notification-receive")
    public ApiResponse<NotificationReceiveResponse> getNotificationReceive(Authentication authentication) {
        return ApiResponse.ok(notificationReceiveService.getNotificationReceive(authentication.getName()));
    }
}
