package com.chatty.service.comment;

import com.chatty.constants.Authority;
import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("댓글을 등록한다.")
    @Test
    void createComment() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("제목", "내용", user);
        postRepository.save(post);

        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글")
                .build();

        // when
        CommentResponse commentResponse = commentService.createComment(post.getId(), request, user.getMobileNumber());

        // then
        assertThat(commentResponse.getCommentId()).isNotNull();
        assertThat(commentResponse)
                .extracting("postId", "userId", "content")
                .containsExactlyInAnyOrder(post.getId(), user.getId(), request.getContent());
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

    private Post createPost(final String title, final String content, final User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }

}