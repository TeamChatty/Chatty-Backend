package com.chatty.dto.notification.receive.response;

import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationReceiveResponse {

    private Long userId;

    private Long notificationReceiveId;

    private boolean marketingNotification;

    private boolean chattingNotification;

    private boolean feedNotification;

    @Builder
    public NotificationReceiveResponse(final Long userId, final Long notificationReceiveId, final boolean marketingNotification, final boolean chattingNotification, final boolean feedNotification) {
        this.userId = userId;
        this.notificationReceiveId = notificationReceiveId;
        this.marketingNotification = marketingNotification;
        this.chattingNotification = chattingNotification;
        this.feedNotification = feedNotification;
    }

    public static NotificationReceiveResponse of(final NotificationReceive notificationReceive) {
        return NotificationReceiveResponse.builder()
                .userId(notificationReceive.getUser().getId())
                .notificationReceiveId(notificationReceive.getId())
                .marketingNotification(notificationReceive.isMarketingNotification())
                .chattingNotification(notificationReceive.isChattingNotification())
                .feedNotification(notificationReceive.isFeedNotification())
                .build();
    }
}
