package com.chatty.service.like;

import com.chatty.constants.Code;
import com.chatty.dto.like.response.PostLikeResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.like.PostLikeRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import com.chatty.service.fcm.FcmService;
import com.chatty.service.notification.NotificationReceiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostLikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final AlarmService alarmService;

    private final NotificationReceiveService notificationReceiveService;
    private final FcmService fcmService;

    @Transactional
    public PostLikeResponse likePost(final Long postId, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new CustomException(Code.ALREADY_LIKE_POST);
        }

        PostLike postLike = postLikeRepository.save(PostLike.builder()
                .post(post)
                .user(user)
                .build());

        if (!post.getUser().getId().equals(user.getId())) {
            alarmService.createLikeAlarm(post.getId(), user.getId(), user.getNickname(), post.getUser());

            NotificationReceiveResponse notificationReceiveResponse =
                    notificationReceiveService.getNotificationReceive(post.getUser().getMobileNumber());

            if (notificationReceiveResponse.isFeedNotification()) {
                fcmService.sendNotificationWithPostLike(post.getUser(), user);
            }
        }

        return PostLikeResponse.of(postLike);
    }

    @Transactional
    public PostLikeResponse unlikePost(final Long postId, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_LIKE_POST));
        postLikeRepository.delete(postLike);

        return PostLikeResponse.of(postLike);
    }
}
