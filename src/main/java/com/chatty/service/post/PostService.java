package com.chatty.service.post;

import com.chatty.constants.Code;
import com.chatty.dto.like.response.PostLikeCountResponse;
import com.chatty.dto.like.response.PostLikeResponse;
import com.chatty.dto.post.request.PostRequest;
import com.chatty.dto.post.response.PostCreateResponse;
import com.chatty.dto.post.response.PostListResponse;
import com.chatty.dto.post.response.PostResponse;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.post.PostImage;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.bookmark.BookmarkRepository;
import com.chatty.repository.like.PostLikeRepository;
import com.chatty.repository.post.PostImageRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.utils.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3service;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BlockRepository blockRepository;

    @Transactional
    public PostCreateResponse createPost(final String mobileNumber, final PostRequest request) throws IOException {
        log.info("createPost Method Start");
        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        Post post = postRepository.save(request.toEntity(user));

        if (request.getImages() == null || request.getImages().isEmpty()) {
            return PostCreateResponse.of(post, user);
        }


        for (MultipartFile image : request.getImages()) {
            validateExtension(image.getOriginalFilename());
            String fileUrl =
                    s3service.uploadFileToS3(image, "post/" + post.getId() + "/" + image.getOriginalFilename());
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .image(fileUrl)
                    .build();
            PostImage savedPostImage = postImageRepository.save(postImage);
            post.getPostImages().add(savedPostImage);
        }

        return PostCreateResponse.of(post, user);
    }

    @Transactional
    public PostResponse getPost(final String mobileNumber, final Long postId) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        boolean isLike = postLikeRepository.existsByPostAndUser(post, user);
        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isBookmark = bookmarkRepository.existsByPostAndUser(post, user);
        post.addViewCount();

        int likeCount = postLikeRepository.countByPost(post);

        return PostResponse.of(post, user, isLike, isOwner, isBookmark, likeCount);
    }

    public List<PostListResponse> getPostList(final String mobileNumber) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        // 로그인 중인 사람 기준으로 차단한 사람을 List<Long> 으로 불러온다.
        List<Long> blockedIds = blockRepository.customFindAllByBlocker(user);

        List<Post> postList;
        // blockedIds 가 null 인 경우에는 쿼리가 제대로 작동하지 않기 때문에 if 조건이 필요하다.
        if (blockedIds == null || blockedIds.isEmpty()) {
            postList = postRepository.findAll(Sort.by("id").descending());
        } else {
            // 불러온 사람들을 제외한 게시글을 검색해야 하기 때문에 where 절에 새로운 조건을 추가한다.
            postList = postRepository.findAllByUserIdNotInOrderByIdDesc(blockedIds);
        }

        return postList.stream()
                .map(post -> PostListResponse.of(post, user))
                .collect(Collectors.toList());
    }

    public List<PostListResponse> getPostListPages(final Long lastPostId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);
        List<Long> blockedIds = blockRepository.customFindAllByBlocker(user);

        Page<Post> posts;
        if (blockedIds == null || blockedIds.isEmpty()) {
            posts = postRepository.findByIdLessThanOrderByIdDesc(lastPostId, pageRequest);
        } else {
            posts = postRepository.findByIdLessThanAndUserIdNotInOrderByIdDesc(lastPostId, blockedIds, pageRequest);
        }

        return posts.getContent().stream()
                .map(post -> PostListResponse.of(post, user))
                .toList();
    }

    public List<PostListResponse> getPostListPagesOrderByTopLiked(final Long lastLikeCount, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);
        log.info("userId = {}", user.getId());
        List<Post> posts = postRepository.customFindByLikeCountLessThanOrderByLikeCountDescAndIdDesc(lastLikeCount, pageRequest);

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();
        List<PostLike> byUserAndPostIds = postLikeRepository.findByUserAndPostIds(user, postIds);
        Map<Long, Boolean> likeMap = new HashMap<>();
        for (PostLike like : byUserAndPostIds) {
            likeMap.put(like.getPost().getId(), true);
        }

        for (Long postId : postIds) {
            likeMap.putIfAbsent(postId, false);
        }

        return posts.stream()
                .map(post -> test2(post, user, likeMap))
                .toList();
    }

    private PostListResponse test2(Post post, User user, Map<Long, Boolean> likeMap) {

        return PostListResponse.
                ofTest2(post, user, likeMap.getOrDefault(post.getId(), false));
    }

    public List<PostListResponse> getMyPostListPages(final Long lastPostId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);
        Page<Post> posts = postRepository.findByUserAndIdLessThanOrderByIdDesc(user, lastPostId, pageRequest);

        return posts.getContent().stream()
                .map(post -> PostListResponse.of(post, user))
                .toList();
    }

    private void validateExtension(final String filename) {
        String[] file = filename.split("\\.");
        String extension = file[file.length - 1];

        if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")) {
            throw new CustomException(Code.INVALID_EXTENSION);
        }
    }
}
