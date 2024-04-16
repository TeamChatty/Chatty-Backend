package com.chatty.entity.notification;

import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class NotificationReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_receive_id")
    private Long id;

    private boolean marketingNotification;

    private boolean chattingNotification;

    private boolean feedNotification;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public NotificationReceive(final boolean marketingNotification, final boolean chattingNotification, final boolean feedNotification, final User user) {
        this.marketingNotification = marketingNotification;
        this.chattingNotification = chattingNotification;
        this.feedNotification = feedNotification;
        this.user = user;
    }

}
