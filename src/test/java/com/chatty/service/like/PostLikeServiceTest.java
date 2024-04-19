package com.chatty.service.like;

import com.chatty.constants.Authority;
import com.chatty.dto.like.response.PostLikeResponse;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.like.PostLikeRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostLikeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("게시글에 좋아요를 누른다.")
    @Test
    void likePost() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("김연아", "01098765432");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("제목", "내용", writer);
        postRepository.save(post);

        // when
        PostLikeResponse postLikeResponse = postLikeService.likePost(post.getId(), user.getMobileNumber());

        // then
        assertThat(postLikeResponse.getPostLikeId()).isNotNull();
        assertThat(postLikeResponse)
                .extracting("postLikeId", "postId", "userId", "writerId")
                .containsExactlyInAnyOrder(
                        postLikeResponse.getPostLikeId(), post.getId(), user.getId(), writer.getId()
                );
    }

    @DisplayName("좋아요를 누른 게시글에 좋아요를 또 누르면 중복 예외가 발생한다.")
    @Test
    void likePostDuplicateLike() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("김연아", "01098765432");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("제목", "내용", writer);
        postRepository.save(post);

        PostLike postLike = createPostLike(user, post);
        postLikeRepository.save(postLike);

        // when // then
        assertThatThrownBy(() -> postLikeService.likePost(post.getId(), user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");
    }

    @DisplayName("게시글에 좋아요를 취소한다.")
    @Test
    void unlikePost() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("김연아", "01098765432");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("제목", "내용", writer);
        postRepository.save(post);

        PostLike postLike = createPostLike(user, post);
        postLikeRepository.save(postLike);

        // when
        postLikeService.unlikePost(post.getId(), user.getMobileNumber());

        // then
        assertThatThrownBy(() -> postLikeRepository.findById(postLike.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("게시글에 좋아요를 취소할 때, 좋아요가 존재하지 않으면 예외가 발생한다.")
    @Test
    void unlikePostWithoutLike() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("김연아", "01098765432");
        userRepository.saveAll(List.of(writer, user));

        Post post = createPost("제목", "내용", writer);
        postRepository.save(post);

        // when // then
        assertThatThrownBy(() -> postLikeService.unlikePost(post.getId(), user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("좋아요가 존재하지 않습니다.");
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

    private PostLike createPostLike(final User user, final Post post) {
        return PostLike.builder()
                .user(user)
                .post(post)
                .build();
    }

}