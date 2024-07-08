package com.chatty.repository.post;

import com.chatty.constants.Code;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_POST));
    }

    Page<Post> findByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);

//    @Query(value = "select p.*, COUNT(pl.post_like_id) as LikeCount " +
//            "from post p " +
//            "inner join post_like pl on p.post_id = pl.post_id " +
//            "group by p.post_id " +
//            "having LikeCount < :lastLikeCount " +
//            "order by LikeCount Desc, p.post_id Desc", nativeQuery = true)
//    List<Post> customFindByLikeCountLessThanOrderByLikeCountDescAndIdDesc(Long lastLikeCount, Pageable pageable);

    @Query(value = "select p.* " +
            "from post p " +
            "order by p.like_count Desc, p.post_id Desc", nativeQuery = true)
    List<Post> customFindByLikeCountLessThanOrderByLikeCountDescAndIdDesc(Long lastLikeCount, Pageable pageable);

//    @Query(value = "select p.*, COUNT(pl.post_like_id) as LikeCount " +
//            "from post p " +
//            "inner join post_like pl on p.post_id = pl.post_id " +
//            "group by p.post_id " +
//            "having LikeCount < :lastLikeCount", nativeQuery = true)
//    List<Post> customFindByLikeCountLessThanOrderByLikeCountDescAndIdDesc(Long lastLikeCount, Pageable pageable);

    Page<Post> findByUserAndIdLessThanOrderByIdDesc(User user, Long postId, Pageable pageable);

    List<Post> findAllByUserIdNotInOrderByIdDesc(List<Long> users);

    Page<Post> findByIdLessThanAndUserIdNotInOrderByIdDesc(Long postId, List<Long> users, Pageable pageable);
}
