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
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import com.chatty.service.fcm.FcmService;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final BlockRepository blockRepository;

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

    public List<CommentListResponse> getCommentListPages(final Long postId, final Long lastCommentId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);
        List<Long> blockedIds = blockRepository.customFindAllByBlocker(user);

        Page<Comment> comments;
        if (blockedIds == null || blockedIds.isEmpty()) {
            comments = commentRepository.findByPostIdAndIdLessThanAndParentIsNullOrderByIdDesc(postId, lastCommentId, pageRequest);
        } else {
            comments =
                    commentRepository.findByPostIdAndIdLessThanAndParentIsNullAndUserIdNotInOrderByIdDesc(postId, lastCommentId, blockedIds, pageRequest);
        }

        return comments.getContent().stream()
                .map(comment -> CommentListResponse.of(comment, user))
                .toList();
    }

    public List<CommentReplyListResponse> getCommentReplyList(final Long postId, final Long commentId, final String mobileNumber) {
//        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

//        Comment comment = commentRepository.getById(commentId);

        List<Comment> result = commentRepository.findAllByParentIdOrderByIdAsc(commentId);

        return result.stream()
                .map(comment -> CommentReplyListResponse.of(comment, user))
                .collect(Collectors.toList());
    }

    public List<CommentReplyListResponse> getCommentReplyListPages(final Long parentId, final Long lastCommentId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);
        List<Long> blockedIds = blockRepository.customFindAllByBlocker(user);

//        Comment parent = commentRepository.getById(commentId);

        Page<Comment> comments;
        if (blockedIds == null || blockedIds.isEmpty()) {
            comments = commentRepository.findByParentIdAndIdGreaterThanOrderByIdAsc(parentId, lastCommentId, pageRequest);
        } else {
            comments =
                    commentRepository.findByParentIdAndIdGreaterThanAndUserIdNotInOrderByIdAsc(parentId, lastCommentId, blockedIds, pageRequest);
        }

        return comments.getContent().stream()
                .map(comment -> CommentReplyListResponse.of(comment, user))
                .collect(Collectors.toList());
    }

    public List<CommentListResponse> getMyCommentListPages(final Long lastCommentId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Page<Comment> comments =
                commentRepository.findByUserAndIdLessThanOrderByIdDesc(user, lastCommentId, pageRequest);

        return comments.getContent().stream()
                .map(comment -> CommentListResponse.of(comment, user))
                .toList();
    }
}
