package com.chatty.service.bookmark;

import com.chatty.constants.Authority;
import com.chatty.dto.bookmark.response.BookmarkListResponse;
import com.chatty.dto.bookmark.response.BookmarkResponse;
import com.chatty.dto.post.response.PostListResponse;
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

    @DisplayName("북마크한 게시글들을 페이징 처리하여 목록을 불러온다.")
    @Test
    void getMyBookmarkListPages() {
        // given
        User user = createUser("박지성", "01012345678");
        User writer = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, writer));

        Post post1 = createPost("안녕하세요1", writer);
        Post post2 = createPost("안녕하세요2", writer);
        Post post3 = createPost("안녕하세요3", writer);
        Post post4 = createPost("안녕하세요4", writer);
        Post post5 = createPost("안녕하세요5", writer);
        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        Bookmark bookmark1 = createBookmark(post5, user);
        Bookmark bookmark2 = createBookmark(post3, user);
        Bookmark bookmark3 = createBookmark(post1, user);
        bookmarkRepository.save(bookmark1);
        bookmarkRepository.save(bookmark2);
        bookmarkRepository.save(bookmark3);
//        bookmarkRepository.saveAll(List.of(bookmark1, bookmark2, bookmark3));

        // when
        List<BookmarkListResponse> bookmarkListResponse =
                bookmarkService.getMyBookmarkListPages(post5.getId() + 1, 3, user.getMobileNumber());
        PostListResponse postListResponse1 = bookmarkListResponse.get(0).getPostListResponse();
        PostListResponse postListResponse2 = bookmarkListResponse.get(1).getPostListResponse();
        PostListResponse postListResponse3 = bookmarkListResponse.get(2).getPostListResponse();

        // then 북마크를 가장 마지막에 누른 게시글은 post1, 따라서 post1이 제일 처음으로 조회된다.
        assertThat(bookmarkListResponse).hasSize(3);
        assertThat(bookmarkListResponse)
                .extracting("postListResponse")
                .containsExactly(
                        postListResponse1, postListResponse2, postListResponse3
                );

        assertThat(postListResponse1.getPostId()).isEqualTo(post1.getId());
        assertThat(postListResponse2.getPostId()).isEqualTo(post3.getId());
        assertThat(postListResponse3.getPostId()).isEqualTo(post5.getId());
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