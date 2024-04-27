package com.chatty.repository.post;

import com.chatty.constants.Code;
import com.chatty.entity.post.Post;
import com.chatty.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_POST));
    }

    Page<Post> findByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);
}
