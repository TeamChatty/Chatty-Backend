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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationWithComment(final User postWriter, final User commentWriter, final String content) throws FirebaseMessagingException {
        if (existDeviceToken(postWriter)) return;
        Notification notification = Notification.builder()
                .setTitle(commentWriter.getNickname() + "님이 댓글을 남겼습니다.")
                .setBody(content)
                .build();

        Message message = Message.builder()
                .setToken(postWriter.getDeviceToken())
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationWithPostLike(final User postWriter, final User commentWriter) throws FirebaseMessagingException {
        if (existDeviceToken(postWriter)) return;
        Notification notification = Notification.builder()
                .setTitle(commentWriter.getNickname())
                .setBody("회원님의 게시글을 좋아합니다.")
                .build();

        Message message = Message.builder()
                .setToken(postWriter.getDeviceToken())
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationWithCommentLike(final User postWriter, final User commentWriter) throws FirebaseMessagingException {
        if (existDeviceToken(postWriter)) return;
        Notification notification = Notification.builder()
                .setTitle(commentWriter.getNickname())
                .setBody("회원님의 댓글을 좋아합니다.")
                .build();

        Message message = Message.builder()
                .setToken(postWriter.getDeviceToken())
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationWithProfileUnlock(final User postWriter, final User commentWriter) throws FirebaseMessagingException {
        if (existDeviceToken(postWriter)) return;
        Notification notification = Notification.builder()
                .setTitle(commentWriter.getNickname())
                .setBody("회원님의 프로필 잠금을 해제했습니다.")
                .build();

        Message message = Message.builder()
                .setToken(postWriter.getDeviceToken())
                .setNotification(notification)
                .build();

        firebaseMessaging.send(message);
    }

    public boolean sendReportNotification(final String reportReason, final Long reportedId, final String adminNumber) {
        if (!adminNumber.equals("01012345678")) return false;
        User admin = userRepository.getByMobileNumber(adminNumber);

        Notification notification = Notification.builder()
                .setTitle(reportedId + "님이 신고당했습니다.")
                .setBody(reportReason)
                .build();

        Message message = Message.builder()
                .setToken(admin.getDeviceToken())
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean existDeviceToken(final User postWriter) {
        if (postWriter.getDeviceToken() == null) {
            return true;
        }
        return false;
    }
}
