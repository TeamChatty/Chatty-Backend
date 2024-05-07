package com.chatty.service.comment;

import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.request.CommentReplyCreateRequest;
import com.chatty.dto.comment.response.CommentListResponse;
import com.chatty.dto.comment.response.CommentReplyListResponse;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import com.chatty.service.fcm.FcmService;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    private final NotificationReceiveService notificationReceiveService;
    private final FcmService fcmService;

    @Transactional
    public CommentResponse createComment(final Long postId, final CommentCreateRequest request, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Comment comment = commentRepository.save(request.toEntity(post, user));

        if (!post.getUser().getId().equals(user.getId())) {
            alarmService.createCommentAlarm(post.getId(), user.getId(), user.getNickname(), post.getUser(), comment.getId());

            NotificationReceiveResponse notificationReceiveResponse =
                    notificationReceiveService.getNotificationReceive(post.getUser().getMobileNumber());

            if (notificationReceiveResponse.isFeedNotification()) {
                fcmService.sendNotificationWithComment(post.getUser(), user, request.getContent());
            }
        }

        return CommentResponse.of(comment, post, user);
    }

    @Transactional
    public CommentResponse createCommentReply(final Long postId, final Long commentId, final CommentReplyCreateRequest request, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Comment parent = commentRepository.getById(commentId);

        Comment comment = commentRepository.save(request.toEntity(post, user, parent));

        if (!parent.getUser().getId().equals(user.getId())) {
            alarmService.createCommentAlarm(post.getId(), user.getId(), user.getNickname(), parent.getUser(), comment.getId());

            NotificationReceiveResponse notificationReceiveResponse =
                    notificationReceiveService.getNotificationReceive(parent.getUser().getMobileNumber());

            if (notificationReceiveResponse.isFeedNotification()) {
                fcmService.sendNotificationWithComment(parent.getUser(), user, request.getContent());
            }

        }

//        if (!post.getUser().getId().equals(user.getId())) {
//            alarmService.createCommentAlarm(post.getId(), user.getId(), user.getNickname(), post.getUser(), comment.getId());
//        }

        return CommentResponse.of(comment, post, user);
    }

    public List<CommentListResponse> getCommentList(final Long postId, final String mobileNumber) {
//        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        List<Comment> result = commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(postId);

        return result.stream()
                .map(comment -> CommentListResponse.of(comment, user))
                .collect(Collectors.toList());
    }

    public List<CommentReplyListResponse> getCommentReplyList(final Long postId, final Long commentId, final String mobileNumber) {
//        Post post = postRepository.getById(postId);

//        User user = userRepository.getByMobileNumber(mobileNumber);

//        Comment comment = commentRepository.getById(commentId);

        List<Comment> result = commentRepository.findAllByParentIdOrderByIdAsc(commentId);

        return result.stream()
                .map(CommentReplyListResponse::of)
                .collect(Collectors.toList());
    }
}
