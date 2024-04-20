package com.chatty.repository.notification;

import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationReceiveRepository extends JpaRepository<NotificationReceive, Long> {

    default NotificationReceive getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    Optional<NotificationReceive> findByUser(User user);
}
