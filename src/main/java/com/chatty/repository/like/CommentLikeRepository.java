package com.chatty.repository.like;

import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentAndUser(Comment comment, User user);

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
