package com.chatty.service.post;

import com.chatty.constants.Authority;
import com.chatty.dto.post.request.PostRequest;
import com.chatty.dto.post.response.PostCreateResponse;
import com.chatty.dto.post.response.PostListResponse;
import com.chatty.dto.post.response.PostResponse;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.post.PostImage;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.repository.like.PostLikeRepository;
import com.chatty.repository.post.PostImageRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.utils.S3Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAllInBatch();
        postImageRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("게시글을 등록한다. 이미지O")
    @Test
    void createPost() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        MockMultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[]{123, 123});
        MockMultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", new byte[]{123, 123});
        MockMultipartFile image3 = new MockMultipartFile("image3", "image3.jpg", "image/jpeg", new byte[]{123, 123});
        when(s3Service.uploadFileToS3(any(MultipartFile.class), anyString()))
                .thenReturn(image1.getName(), image2.getName(), image3.getName());

        PostRequest request = PostRequest.builder()
                .title("제목")
                .content("내용")
                .images(List.of(image1, image2, image3))
                .build();

        // when
        PostCreateResponse postCreateResponse = postService.createPost(user.getMobileNumber(), request);

        // then
        assertThat(postCreateResponse.getId()).isNotNull();
        assertThat(postCreateResponse.getViewCount()).isZero();
        assertThat(postCreateResponse.getPostImages()).hasSize(3)
                .containsExactlyInAnyOrder(
                        "image1", "image2", "image3"
                );
    }

    @DisplayName("게시글을 등록한다. 이미지X")
    @Test
    void createPostWithoutImage() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        PostRequest request = PostRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // when
        PostCreateResponse postCreateResponse = postService.createPost(user.getMobileNumber(), request);

        // then
        assertThat(postCreateResponse.getId()).isNotNull();
    }

    @DisplayName("게시글을 단건 조회한다. 조회수도 1 증가한다.")
    @Test
    void getPost() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("제목", "내용", user);
        postRepository.save(post);

        PostImage postImage = PostImage.builder()
                .image("image1")
                .post(post)
                .build();
        postImageRepository.save(postImage);
        post.getPostImages().add(postImage);

        // when
        PostResponse postResponse = postService.getPost(user.getMobileNumber(), post.getId());

        // then
        assertThat(postResponse.getPostId()).isNotNull();
        assertThat(postResponse.getTitle()).isEqualTo(post.getTitle());
        assertThat(postResponse)
                .extracting("postId", "content", "isOwner")
                .containsExactlyInAnyOrder(
                        post.getId(), post.getContent(), true
                );
        assertThat(postResponse.getPostImages()).hasSize(1)
                .containsExactlyInAnyOrder(
                        "image1"
                );
        assertThat(postResponse.getViewCount()).isEqualTo(1);
    }

    @DisplayName("게시글 목록을 조회한다.")
    @Test
    void getPostList() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post1 = createPost("제목1", "내용1", user);
        Post post2 = createPost("제목2", "내용2", user);
        Post post3 = createPost("제목3", "내용3", user);
        postRepository.saveAll(List.of(post1, post2, post3));

        // when
        List<PostListResponse> postList = postService.getPostList(user.getMobileNumber());

        // then
        assertThat(postList).hasSize(3)
                .extracting("title", "content", "isOwner", "isLike")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1", true, false),
                        tuple("제목2", "내용2", true, false),
                        tuple("제목3", "내용3", true, false)
                );
    }

    @DisplayName("게시글 목록을 조회한다. 좋아요 여부, 글을 쓴 사람인지 여부도 확인할 수 있다.")
    @Test
    void getPostListWithIsLikeAndIsOwner() throws IOException {
        // given
        // user와 user2가 쓴 글이 3개씩 존재하고 user2가 작성한 글 2개(4, 5)에만 좋아요를 누른 상황이다. user를 기준으로 조회한다.
        User user = createUser("닉네임", "01012345678");
        User user2 = createUser("닉네임2", "01011112222");
        userRepository.saveAll(List.of(user, user2));

        Post post1 = createPost("제목1", "내용1", user);
        Post post2 = createPost("제목2", "내용2", user);
        Post post3 = createPost("제목3", "내용3", user);
        Post post4 = createPost("제목4", "내용4", user2);
        Post post5 = createPost("제목5", "내용5", user2);
        Post post6 = createPost("제목6", "내용6", user2);
        postRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6));

        PostLike postLike1 = createPostLike(post4, user);
        PostLike postLike2 = createPostLike(post5, user);
        postLikeRepository.saveAll(List.of(postLike1, postLike2));

        // when
        List<PostListResponse> postList = postService.getPostList(user.getMobileNumber());

        // then
        assertThat(postList).hasSize(6)
                .extracting("title", "content", "isOwner", "isLike")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1", true, false),
                        tuple("제목2", "내용2", true, false),
                        tuple("제목3", "내용3", true, false),
                        tuple("제목4", "내용4", false, true),
                        tuple("제목5", "내용5", false, true),
                        tuple("제목6", "내용6", false, false)
                );
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

    private PostLike createPostLike(final Post post, final User user) {
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }
}