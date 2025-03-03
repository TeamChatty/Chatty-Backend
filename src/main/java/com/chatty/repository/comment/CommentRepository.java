package com.chatty.repository.comment;

import com.chatty.constants.Code;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_COMMENT));
    }

    List<Comment> findAllByPostIdAndParentIsNullOrderByIdDesc(Long postId);
    List<Comment> findAllByParentIdOrderByIdAsc(Long parentId);

    Page<Comment> findByPostIdAndIdLessThanAndParentIsNullOrderByIdDesc(Long postId, Long lastCommentId, Pageable pageable);
    Page<Comment> findByPostIdAndIdLessThanAndParentIsNullAndUserIdNotInOrderByIdDesc(Long postId, Long lastCommentId, List<Long> users, Pageable pageable);

    Page<Comment> findByParentIdAndIdGreaterThanOrderByIdAsc(Long parentId, Long lastCommentId, Pageable pageable);

    Page<Comment> findByParentIdAndIdGreaterThanAndUserIdNotInOrderByIdAsc(Long parentId, Long lastCommentId, List<Long> users, Pageable pageable);

    Page<Comment> findByUserAndIdLessThanOrderByIdDesc(User user, Long commentId, Pageable pageable);
}
