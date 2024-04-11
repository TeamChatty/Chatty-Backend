package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleMessageResponseDto {

    private Long messageId;

    private Long roomId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private LocalDateTime sendTime;

    public static SimpleMessageResponseDto of(ChatMessage chatMessage){
        return SimpleMessageResponseDto.builder()
                .messageId(chatMessage.getMessageId())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .senderId(chatMessage.getSender().getId())
                .receiverId(chatMessage.getReceiver().getId())
                .content(chatMessage.getContent())
                .sendTime(chatMessage.getSendTime())
                .build();
    }
}
