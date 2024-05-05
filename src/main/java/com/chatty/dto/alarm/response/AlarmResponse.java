package com.chatty.dto.alarm.response;

import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.alarm.AlarmType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlarmResponse {

    private Long alarmId;

    private AlarmType alarmType;

    private String content;

    private Long fromUserId;

    private Long toUserId;

    private boolean isRead;

    @Builder
    public AlarmResponse(final Long alarmId, final AlarmType alarmType, final String content, final Long fromUserId, final Long toUserId, final boolean isRead) {
        this.alarmId = alarmId;
        this.alarmType = alarmType;
        this.content = content;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.isRead = isRead;
    }

    public static AlarmResponse of(final Alarm alarm) {
        return AlarmResponse.builder()
                .alarmId(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .content(alarm.getContent())
                .fromUserId(alarm.getFromUser())
                .toUserId(alarm.getUser().getId())
                .isRead(alarm.isRead())
                .build();
    }
}
