package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    private Long roomId;
    private Long senderId;
    private Long receiverId;

    public static RoomResponseDto of(ChatRoom chatRoom){
        return RoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getReceiver().getId())
                .build();
    }
}
