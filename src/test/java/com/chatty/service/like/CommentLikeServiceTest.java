package com.chatty.service.like;

import com.chatty.constants.Authority;
import com.chatty.dto.like.response.CommentLikeResponse;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.alarm.AlarmRepository;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.like.CommentLikeRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.notification.NotificationReceiveService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommentLikeServiceTest {

    @Autowired
    private CommentLikeService commentLikeService;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private NotificationReceiveService notificationReceiveService;

    @AfterEach
    void tearDown() {
        alarmRepository.deleteAllInBatch();
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("댓글에 좋아요를 누른다.")
    @Test
    void likeComment() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("글내용", writer);
        postRepository.save(post);

        Comment comment = createComment(null, "댓글내용", writer, post);
        commentRepository.save(comment);

        // stubbing
        when(notificationReceiveService.getNotificationReceive(any(String.class)))
                .thenReturn(NotificationReceiveResponse.builder()
                        .userId(post.getId())
                        .feedNotification(false)
                        .marketingNotification(false)
                        .chattingNotification(false)
                        .build());

        // when
        CommentLikeResponse commentLikeResponse =
                commentLikeService.likeComment(comment.getId(), user.getMobileNumber());

        // then
        assertThat(commentLikeResponse.getCommentLikeId()).isNotNull();
        assertThat(commentLikeResponse)
                .extracting("commentLikeId", "writerId", "userId", "commentId")
                .containsExactlyInAnyOrder(
                        commentLikeResponse.getCommentLikeId(), writer.getId(), user.getId(), comment.getId()
                );
    }

    @DisplayName("좋아요를 누른 댓글에 좋아요를 또 누르면 중복 예외가 발생한다.")
    @Test
    void likeCommentWithDuplicateCommentLike() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("글내용", writer);
        postRepository.save(post);

        Comment comment = createComment(null, "댓글내용", writer, post);
        commentRepository.save(comment);

        CommentLike commentLike = createCommentLike(user, comment);
        commentLikeRepository.save(commentLike);

        // when // then
        assertThatThrownBy(() -> commentLikeService.likeComment(comment.getId(), user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");
    }

    @DisplayName("댓글 좋아요를 취소한다.")
    @Test
    void unlikeComment() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("글내용", writer);
        postRepository.save(post);

        Comment comment = createComment(null, "댓글내용", writer, post);
        commentRepository.save(comment);

        CommentLike commentLike = createCommentLike(user, comment);
        commentLikeRepository.save(commentLike);

        // when
        commentLikeService.unlikeComment(comment.getId(), user.getMobileNumber());

        // then
        assertThatThrownBy(() -> commentLikeRepository.findById(commentLike.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("댓글 좋아요를 취소할 때, 좋아요가 존재하지 않으면 예외가 발생한다.")
    @Test
    void unlikeCommentWithoutLike() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("글내용", writer);
        postRepository.save(post);

        Comment comment = createComment(null, "댓글내용", writer, post);
        commentRepository.save(comment);

        CommentLike commentLike = createCommentLike(user, comment);
        commentLikeRepository.save(commentLike);

        // when
        commentLikeService.unlikeComment(comment.getId(), user.getMobileNumber());

        // then
        assertThatThrownBy(() -> commentLikeRepository.findById(commentLike.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    private User createUser(final String nickname, final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
//                .mbti(Mbti.ENFJ)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .location(User.createPoint(
                        Coordinate.builder()
                                .lat(37.1)
                                .lng(127.1)
                                .build()
                ))
                .build();
    }

    private Post createPost(final String content, final User user) {
        return Post.builder()
                .content(content)
                .user(user)
                .build();
    }

    private Comment createComment(final Comment parent, final String content, final User user, final Post post) {
        return Comment.builder()
                .parent(parent)
                .post(post)
                .user(user)
                .content(content)
                .build();
    }

    private CommentLike createCommentLike(final User user, final Comment comment) {
        return CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
    }
}