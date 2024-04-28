package com.chatty.service.fcm;

import com.chatty.constants.Code;
import com.chatty.dto.fcm.request.FcmRequest;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.user.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    public String sendNotification(final FcmRequest request) throws FirebaseMessagingException {
        Long userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();

        Message message = Message.builder()
                .setToken(user.getDeviceToken())
                .setNotification(notification)
                .build();

        return firebaseMessaging.send(message);
    }
}
