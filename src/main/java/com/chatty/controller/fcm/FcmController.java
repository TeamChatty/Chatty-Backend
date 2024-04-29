package com.chatty.controller.fcm;

import com.chatty.dto.fcm.request.FcmRequest;
import com.chatty.service.fcm.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/v1/notification")
    public String sendNotification(@RequestBody FcmRequest request) throws FirebaseMessagingException {
        return fcmService.sendNotification(request);
    }
}
