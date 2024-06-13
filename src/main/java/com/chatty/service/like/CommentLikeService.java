package com.chatty.service.like;

import com.chatty.constants.Code;
import com.chatty.dto.like.response.CommentLikeResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.alarm.AlarmRepository;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.like.CommentLikeRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import com.chatty.service.fcm.FcmService;
import com.chatty.service.notification.NotificationReceiveService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;

    private final NotificationReceiveService notificationReceiveService;
    private final FcmService fcmService;

    @Transactional
    public CommentLikeResponse likeComment(final Long commentId, final String mobileNumber) {
        Comment comment = commentRepository.getById(commentId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new CustomException(Code.ALREADY_LIKE_COMMENT);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        commentLikeRepository.save(commentLike);

        if (!comment.getUser().getId().equals(user.getId())) {
            try {
                alarmService.createCommentLikeAlarm(comment.getId(), user.getId(), user.getNickname(), comment.getUser());
            } catch (RuntimeException e) {
                log.info("commentLikeService - createCommentLikeAlarm 예외 발생");
            }

            NotificationReceiveResponse notificationReceiveResponse =
                    notificationReceiveService.getNotificationReceive(comment.getUser().getMobileNumber());

            if (notificationReceiveResponse.isFeedNotification()) {
                try {
                    fcmService.sendNotificationWithCommentLike(comment.getUser(), user);
                } catch (FirebaseMessagingException e) {
                    log.info("commentLikeService - fcmService 예외 발생");
                }
            }
        }

        return CommentLikeResponse.of(commentLike);
    }

    @Transactional
    public CommentLikeResponse unlikeComment(final Long commentId, final String mobileNumber) {
        Comment comment = commentRepository.getById(commentId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_LIKE_COMMENT));
        commentLikeRepository.delete(commentLike);

        alarmRepository.findByCommentIdAndUserIdAndFromUser(commentId, comment.getUser().getId(), user.getId())
                .ifPresent(alarmRepository::delete);

        return CommentLikeResponse.of(commentLike);
    }
}
