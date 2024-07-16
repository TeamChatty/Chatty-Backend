package com.chatty.repository.bookmark;

import com.chatty.entity.bookmark.Bookmark;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<Bookmark> findByPostAndUser(Post post, User user);

    List<Bookmark> findByUserAndIdLessThanOrderByCreatedAtDesc(User user, Long lastBookmarkId, Pageable pageable);
}
