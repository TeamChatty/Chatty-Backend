package com.chatty.service.bookmark;

import com.chatty.constants.Authority;
import com.chatty.dto.bookmark.response.BookmarkResponse;
import com.chatty.entity.bookmark.Bookmark;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.bookmark.BookmarkRepository;
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
class BookmarkServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @AfterEach
    void tearDown() {
        bookmarkRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("게시글을 북마크한다.")
    @Test
    void createBookmark() {
        // given
        User user = createUser("박지성", "01012345678");
        User writer = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, writer));

        Post post = createPost("안녕하세요", writer);
        postRepository.save(post);

        // when
        BookmarkResponse bookmarkResponse = bookmarkService.createBookmark(post.getId(), user.getMobileNumber());

        // then
        assertThat(bookmarkResponse.getBookmarkId()).isNotNull();
        assertThat(bookmarkResponse)
                .extracting("writerId", "postId", "userId")
                .containsExactlyInAnyOrder(
                        writer.getId(), post.getId(), user.getId()
                );
    }

    @DisplayName("게시글을 북마크할 때, 이미 내역이 존재하면 예외가 발생한다.")
    @Test
    void createBookmarkWithDuplicateBookmark() {
        // given
        User user = createUser("박지성", "01012345678");
        User writer = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, writer));

        Post post = createPost("안녕하세요", writer);
        postRepository.save(post);

        Bookmark bookmark = createBookmark(post, user);
        bookmarkRepository.save(bookmark);

        // when // then
        assertThatThrownBy(() -> bookmarkService.createBookmark(post.getId(), user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("북마크가 이미 존재합니다.");
    }

    @DisplayName("게시글 북마크를 취소한다.")
    @Test
    void deleteBookmark() {
        // given
        User user = createUser("박지성", "01012345678");
        User writer = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, writer));

        Post post = createPost("안녕하세요", writer);
        postRepository.save(post);

        Bookmark bookmark = createBookmark(post, user);
        bookmarkRepository.save(bookmark);

        // when
        bookmarkService.deleteBookmark(post.getId(), user.getMobileNumber());

        // then
        assertThatThrownBy(() -> bookmarkRepository.findById(bookmark.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("게시글 북마크를 취소할 때, 북마크 내역이 존재하지 않으면 예외가 발생한다.")
    @Test
    void deleteBookmarkWithoutBookmark() {
        // given
        User user = createUser("박지성", "01012345678");
        User writer = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, writer));

        Post post = createPost("안녕하세요", writer);
        postRepository.save(post);

        // when // then
        assertThatThrownBy(() -> bookmarkService.deleteBookmark(post.getId(), user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("북마크가 존재하지 않습니다.");
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

    private Bookmark createBookmark(final Post post, final User user) {
        return Bookmark.builder()
                .post(post)
                .user(user)
                .build();
    }
}