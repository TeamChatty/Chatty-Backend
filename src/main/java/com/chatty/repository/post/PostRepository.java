package com.chatty.repository.post;

import com.chatty.constants.Code;
import com.chatty.entity.post.Post;
import com.chatty.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_POST));
    }

    Page<Post> findByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);

    @Query(value = "select p.*, COUNT(pl.post_like_id) as LikeCount " +
            "from Post p " +
            "inner join Post_Like pl on p.post_id = pl.post_id " +
            "group by p.post_id " +
            "having LikeCount < :lastLikeCount " +
            "order by LikeCount Desc, p.post_id Desc", nativeQuery = true)
    Page<Post> customFindByLikeCountLessThanOrderByLikeCountDescAndIdDesc(Long lastLikeCount, Pageable pageable);
}
