package com.chatty.dto.alarm.response;

import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.alarm.AlarmType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlarmListResponse {

    private Long alarmId;

    private Long postId;

    private Long commentId;

    private Long fromUserId;

    private LocalDateTime registeredDateTime;

    private AlarmType alarmType;

    private String content;

    @Builder
    public AlarmListResponse(final Long alarmId, final Long postId, final Long commentId, final Long fromUserId, final LocalDateTime registeredDateTime, final AlarmType alarmType, final String content) {
        this.alarmId = alarmId;
        this.postId = postId;
        this.commentId = commentId;
        this.fromUserId = fromUserId;
        this.registeredDateTime = registeredDateTime;
        this.alarmType = alarmType;
        this.content = content;
    }

    public static AlarmListResponse of(final Alarm alarm) {
        return AlarmListResponse.builder()
                .alarmId(alarm.getId())
                .postId(alarm.getPostId())
                .alarmType(alarm.getAlarmType())
                .commentId(alarm.getCommentId())
                .fromUserId(alarm.getFromUser())
                .registeredDateTime(alarm.getCreatedAt())
                .content(alarm.getContent())
                .build();
    }
}
