package com.chatty.repository.comment;

import com.chatty.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    default Comment getById(Long id) {
//        return findById(id)
//                .orElseThrow(() -> new RuntimeException("Comment not found"));
//    }
}
