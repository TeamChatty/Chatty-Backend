package com.chatty.repository.alarm;

import com.chatty.constants.Code;
import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    default Alarm getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_ALARM));
    }

    Page<Alarm> findByIdLessThanAndUserOrderByIdDesc(Long id, User user, PageRequest pageRequest);

    List<Alarm> findAllByUserAndIsReadIsFalse(User user);

    Optional<Alarm> findByPostIdAndUserIdAndFromUser(Long postId, Long userId, Long fromUser);

    Optional<Alarm> findByCommentIdAndUserIdAndFromUser(Long commentId, Long userId, Long fromUser);

}
