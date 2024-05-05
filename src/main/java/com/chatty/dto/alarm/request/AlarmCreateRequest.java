package com.chatty.dto.alarm.request;

import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.alarm.AlarmType;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlarmCreateRequest {

    private AlarmType alarmType;

    private Long fromUser;

    private String content;

    private Long toUser;

    private boolean isRead;

    @Builder
    public AlarmCreateRequest(final AlarmType alarmType, final Long fromUser, final String content, final Long toUser, final boolean isRead) {
        this.alarmType = alarmType;
        this.fromUser = fromUser;
        this.content = content;
        this.toUser = toUser;
        this.isRead = isRead;
    }

    public Alarm toEntity(final User user) {
        return Alarm.builder()
                .user(user)
                .alarmType(alarmType)
                .content(content)
                .isRead(isRead)
                .fromUser(fromUser)
                .build();
    }
}
