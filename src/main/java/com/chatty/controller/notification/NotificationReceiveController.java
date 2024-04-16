package com.chatty.controller.notification;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.notification.receive.request.NotificationReceiveUpdateRequest;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PutMapping("/v1/notification-receive/marketing")
    public ApiResponse<NotificationReceiveResponse> updateMarketingNotificationReceive(@RequestBody NotificationReceiveUpdateRequest request,
                                                                                 Authentication authentication) {
        return ApiResponse.ok(notificationReceiveService.updateMarketingNotification(authentication.getName(), request));
    }

    @PutMapping("/v1/notification-receive/chatting")
    public ApiResponse<NotificationReceiveResponse> updateChattingNotificationReceive(@RequestBody NotificationReceiveUpdateRequest request,
                                                                                 Authentication authentication) {
        return ApiResponse.ok(notificationReceiveService.updateChattingNotification(authentication.getName(), request));
    }

    @PutMapping("/v1/notification-receive/feed")
    public ApiResponse<NotificationReceiveResponse> updateFeedNotificationReceive(@RequestBody NotificationReceiveUpdateRequest request,
                                                                                 Authentication authentication) {
        return ApiResponse.ok(notificationReceiveService.updateFeedNotification(authentication.getName(), request));
    }
}
