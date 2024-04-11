package com.chatty.repository.comment;

import com.chatty.constants.Code;
import com.chatty.entity.comment.Comment;
import com.chatty.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_COMMENT));
    }

    List<Comment> findAllByPostIdAndParentIsNullOrderByIdDesc(Long postId);
    List<Comment> findAllByParentIdOrderByIdAsc(Long parentId);
}
