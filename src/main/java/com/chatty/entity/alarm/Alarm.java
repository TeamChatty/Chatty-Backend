package com.chatty.entity.alarm;

import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long fromUser;

    private String content;

    private boolean isRead;

    private Long postId;

    private Long commentId;

    @Builder
    public Alarm(final User user, final AlarmType alarmType, final Long fromUser, final String content, final boolean isRead, final Long postId, final Long commentId) {
        this.user = user;
        this.alarmType = alarmType;
        this.fromUser = fromUser;
        this.content = content;
        this.isRead = isRead;
        this.postId = postId;
        this.commentId = commentId;
    }

    public void readAlarm(final boolean read) {
        this.isRead = read;
    }
}
