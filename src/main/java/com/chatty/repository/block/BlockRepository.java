package com.chatty.repository.block;

import com.chatty.entity.block.Block;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    @Query("select b.blocked.id " +
            "from Block b " +
            "where b.blocker = :blocker")
    List<Long> customFindAllByBlocker(User blocker);

    List<Block> findAllByBlockerOrderById(User blocker);
}
