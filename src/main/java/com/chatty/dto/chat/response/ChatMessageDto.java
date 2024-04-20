//package com.chatty.dto.chat.response;
//import com.chatty.entity.chat.ChatMessage;
//import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ChatMessageDto {
//
//    private Long messageId;
//
//    private String content;
//
//    private LocalDateTime sendTime;
//
//    private Long senderId;
//
//    private Long receiverId;
//
//    private Long roomId;
//
//    public static ChatMessageDto to(ChatMessage chatMessage){
//        return ChatMessageDto.builder()
//                .messageId(chatMessage.getMessageId())
//                .content(chatMessage.getContent())
//                .sendTime(chatMessage.getSendTime())
//                .senderId(chatMessage.getSender().getId())
//                .receiverId(chatMessage.getReceiver().getId())
//                .roomId(chatMessage.getChatRoom().getRoomId())
//                .build();
//    }
//}
