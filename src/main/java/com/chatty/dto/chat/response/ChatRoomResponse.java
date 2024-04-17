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

    private boolean extend;

    @Builder
    public ChatRoomResponse(final Long roomId, final Long senderId, final Long receiverId, final boolean extend) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.extend = extend;
    }

    public static ChatRoomResponse of(final ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .extend(chatRoom.isExtend())
                .build();
    }
}
