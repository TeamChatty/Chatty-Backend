package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatMessage;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageListResponse {

    private Long chatMessageId;

    private Long roomId;

    private Long yourId;

    private String yourNickname;

    private String yourImageUrl;

    private boolean yourBlueCheck;

    private Long senderId;

    private String content;

    private LocalDateTime sendTime;

    private boolean yourIsRead;

    private LocalDateTime chatRoomCreatedTime;

    private boolean isExtend;

    @Builder
    public ChatMessageListResponse(final Long chatMessageId, final Long roomId, final Long yourId, final String yourNickname, final String yourImageUrl, final boolean yourBlueCheck, final Long senderId, final String content, final LocalDateTime sendTime, final boolean yourIsRead, final LocalDateTime chatRoomCreatedTime, final boolean isExtend) {
        this.chatMessageId = chatMessageId;
        this.roomId = roomId;
        this.yourId = yourId;
        this.yourNickname = yourNickname;
        this.yourImageUrl = yourImageUrl;
        this.yourBlueCheck = yourBlueCheck;
        this.senderId = senderId;
        this.content = content;
        this.sendTime = sendTime;
        this.yourIsRead = yourIsRead;
        this.chatRoomCreatedTime = chatRoomCreatedTime;
        this.isExtend = isExtend;
    }

    public static ChatMessageListResponse of(final ChatMessage chatMessage, final User you) {
        return ChatMessageListResponse.builder()
                .chatMessageId(chatMessage.getMessageId())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .yourId(you.getId())
                .yourNickname(you.getNickname())
                .yourImageUrl(you.getImageUrl())
                .yourBlueCheck(you.isBlueCheck())
                .senderId(chatMessage.getSender().getId())
                .content(chatMessage.getContent())
                .sendTime(chatMessage.getSendTime())
                .yourIsRead(chatMessage.getIsRead())
                .chatRoomCreatedTime(chatMessage.getChatRoom().getCreatedAt())
                .isExtend(chatMessage.getChatRoom().isExtend())
                .build();
    }
}
