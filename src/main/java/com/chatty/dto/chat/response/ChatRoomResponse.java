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

    @Builder
    public ChatRoomResponse(final Long roomId, final Long senderId, final Long receiverId) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public static ChatRoomResponse of(final ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .build();
    }
}
