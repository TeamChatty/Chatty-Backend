package com.chatty.repository.like;

import com.chatty.dto.like.response.PostLikeCountResponse;
import com.chatty.dto.like.response.PostLikeResponse;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    int countByPost(Post post);

    @Query("select pl " +
            "from PostLike pl " +
            "where pl.user = :user " +
            "and pl.post.id in :postIds")
    List<PostLike> findByUserAndPostIds(User user, List<Long> postIds);

    @Query("select new com.chatty.dto.like.response.PostLikeCountResponse(pl.post.id, count(*)) " +
            "from PostLike pl " +
            "where pl.post.id in :postIds " +
            "group by pl.post.id")
    List<PostLikeCountResponse> findByPostIdsAndLikeCount(List<Long> postIds);
}
