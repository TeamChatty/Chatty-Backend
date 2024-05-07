package com.chatty.service.like;

import com.chatty.constants.Code;
import com.chatty.dto.like.response.CommentLikeResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.like.CommentLikeRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import com.chatty.service.fcm.FcmService;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AlarmService alarmService;

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
            alarmService.createCommentLikeAlarm(comment.getId(), user.getId(), user.getNickname(), comment.getUser());

            NotificationReceiveResponse notificationReceiveResponse =
                    notificationReceiveService.getNotificationReceive(comment.getUser().getMobileNumber());

            if (notificationReceiveResponse.isFeedNotification()) {
                fcmService.sendNotificationWithPostLike(comment.getUser(), user);
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

        return CommentLikeResponse.of(commentLike);
    }
}
