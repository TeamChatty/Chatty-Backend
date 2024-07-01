package com.chatty.repository.like;

import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    int countByPost(Post post);
}
