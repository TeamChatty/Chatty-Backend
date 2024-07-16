package com.chatty.service.bookmark;

import com.chatty.constants.Code;
import com.chatty.dto.bookmark.response.BookmarkListResponse;
import com.chatty.dto.bookmark.response.BookmarkResponse;
import com.chatty.dto.post.response.PostListResponse;
import com.chatty.entity.bookmark.Bookmark;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.bookmark.BookmarkRepository;
import com.chatty.repository.like.PostLikeRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public BookmarkResponse createBookmark(final Long postId, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        if (bookmarkRepository.existsByPostAndUser(post, user)) {
            throw new CustomException(Code.ALREADY_EXIST_BOOKMARK);
        }

        Bookmark bookmark = Bookmark.create(post, user);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return BookmarkResponse.of(savedBookmark);
    }

    @Transactional
    public BookmarkResponse deleteBookmark(final Long postId, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Bookmark bookmark = bookmarkRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new CustomException(Code.NOT_FOUND_BOOKMARK));
        bookmarkRepository.delete(bookmark);

        return BookmarkResponse.of(bookmark);
    }

    public List<BookmarkListResponse> getMyBookmarkListPages(final Long lastBookmarkId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);
        User user = userRepository.getByMobileNumber(mobileNumber);

        List<Bookmark> bookmarks =
                bookmarkRepository.findByUserAndIdLessThanOrderByCreatedAtDesc(user, lastBookmarkId, pageRequest);

        List<Long> postIds = bookmarks.stream()
                .map(bookmark -> bookmark.getPost().getId())
                .toList();
        List<PostLike> byUserAndPostIds = postLikeRepository.findByUserAndPostIds(user, postIds);
        Map<Long, Boolean> likeMap = new HashMap<>();
        for (PostLike like : byUserAndPostIds) {
            likeMap.put(like.getPost().getId(), true);
        }

        for (Long postId : postIds) {
            likeMap.putIfAbsent(postId, false);
        }

        return bookmarks.stream()
                .map(bookmark -> of(bookmark, user, likeMap))
                .toList();
    }

    private BookmarkListResponse of(Bookmark bookmark, User user, Map<Long, Boolean> likeMap) {

        return BookmarkListResponse.
                of(bookmark, user, likeMap.getOrDefault(bookmark.getPost().getId(), false));
    }
}
