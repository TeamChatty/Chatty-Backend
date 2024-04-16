package com.chatty.repository.notification;

import com.chatty.entity.notification.NotificationReceive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationReceiveRepository extends JpaRepository<NotificationReceive, Long> {
}
