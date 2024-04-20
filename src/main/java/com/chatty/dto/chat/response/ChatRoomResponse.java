package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomResponse {

    private Long roomId;

    private Long senderId;

    private Long receiverId;

    private boolean isExtend;

    @Builder
    public ChatRoomResponse(final Long roomId, final Long senderId, final Long receiverId, final boolean isExtend) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isExtend = isExtend;
    }

    public static ChatRoomResponse of(final ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .isExtend(chatRoom.isExtend())
                .build();
    }
}
