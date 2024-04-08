package com.chatty.service.chat;

import com.chatty.constants.Code;
import com.chatty.dto.chat.request.ChatMessageRequest;
import com.chatty.dto.chat.request.UnreadMessageDto;
import com.chatty.dto.chat.response.*;
import com.chatty.entity.chat.ChatMessage;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.chat.MessageRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    private final UserService userService;

    @Transactional
    public SimpleMessageResponseDto saveMessage(final Long roomId, final ChatMessageRequest request, final String mobileNumber, final LocalDateTime now){

        User sender = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId)
                .orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));

        if (!chatRoomRepository.existsByRoomIdAndSenderOrReceiver(roomId, sender, sender)) {
            throw new CustomException(Code.NOT_IN_USER_ROOM);
        }
//        chatRoomRepository.findChatRoomByRoomIdAndSenderOrReceiver(roomId, sender, sender)
//                .orElseThrow(() -> new CustomException(Code.NOT_IN_USER_ROOM));

        User receiver = chatRoom.getSender().equals(sender) ? chatRoom.getReceiver() : chatRoom.getSender();

        ChatMessage chatMessage = messageRepository.save(request.toEntity(sender, receiver, chatRoom, now));

        return SimpleMessageResponseDto.of(chatMessage);
    }

//    @Transactional
//    public SimpleMessageResponseDto saveMessage(Long roomId, MessageDto messageDto){
//
//        Long senderId = messageDto.getSenderId();
//
//        isExistedRoomByRoomId(roomId);
//        isExistedSender(senderId);
//
//        User sender = User.builder().id(messageDto.getSenderId()).build();
//        User receiver = User.builder().id(messageDto.getReceiverId()).build();
//        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));
//
//        ChatMessage chatMessage = messageRepository.save(ChatMessage.to(chatRoom, sender, receiver, messageDto.getContent()));
//
//        return SimpleMessageResponseDto.of(chatMessage);
//    }

    /**
     * 내가 보낸 채팅은 sender 로 저장한다.
     * mobileNumber 를 이용하여 내가 보낸 메세지들과 상대방이 보낸 메세지를 구분하자.
     */
    @Transactional
    public List<ChatMessageListResponse> getMessages(Long roomId, String mobileNumber) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId)
                .orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));

        User me = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        if (!chatRoom.getSender().equals(me) && !chatRoom.getReceiver().equals(me)) {
            throw new CustomException(Code.NOT_IN_USER_ROOM);
        }

        // chatRoom 에는 2명이 존재하는데, sender, receiver
        // me 와 sender 같으면 receiver 가 상대방이고, 다르면 sender 가 상대방이다.
        User you = chatRoom.getSender().equals(me) ? chatRoom.getReceiver() : chatRoom.getSender();

        List<ChatMessage> chatMessageList = messageRepository.findAllByChatRoomOrderByMessageIdDesc(chatRoom);
        // 마지막 메세지가 내가 보낸 것이고 상대방이 읽었을 때, isRead 는 true 값을 넘겨준다.
        // 마지막 메세지가 내가 보낸 것이 아닌 경우에는 false 값을 넘겨준다.
//        if (chatMessageList != null && !chatMessageList.isEmpty()) {
//            User lastSender = chatMessageList.get(0).getSender();
//            System.out.println("lastSender.getNickname() = " + lastSender.getNickname());
//            if (lastSender.equals(me)) {
//
//            }
//        }

        List<ChatMessageListResponse> result = chatMessageList.stream()
                .map(m -> ChatMessageListResponse.of(m, you))
                .toList();

        return result;
    }

    @Transactional
    public void acknowledge(final Long roomId, final String mobileNumber) {
        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId)
                .orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));

        if (!chatRoom.getSender().equals(user) && !chatRoom.getReceiver().equals(user)) {
            throw new CustomException(Code.NOT_FOUND_CHAT_ROOM);
        }

        List<ChatMessage> chatMessageList = messageRepository.findAllByChatRoomAndSenderNotAndIsReadIsFalse(chatRoom, user);
        for (ChatMessage chatMessage : chatMessageList) {
            chatMessage.setIsRead();
        }
    }

//    @Transactional
//    public MessageMarkResponseDto markMessageAsRead(Long messageId){
//        ChatMessage message = messageRepository.findChatMessagesByMessageId(messageId).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_MESSAGE));
//        message.setIsRead();
//
//        return MessageMarkResponseDto.of(message.getMessageId(), message.getIsRead());
//    }

//    @Transactional
//    public MultipleMessageResponseDto getMessages(UnreadMessageDto unreadMessageDto){ // 읽지 않은 메세지 위주로
//
//        List<ChatMessage> messages = messageRepository.findByIsReadFalseAndChatRoomRoomIdOrderBySendTimeDesc(unreadMessageDto.getRoomId()).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_MESSAGE));
//
//        List<ChatMessageDto> result = messages.stream().map(message -> ChatMessageDto.to(message)).collect(Collectors.toList());
//
//        return MultipleMessageResponseDto.of(result);
//    }

    private void isExistedRoomByRoomId(Long roomId){
        chatRoomRepository.findChatRoomByRoomId(roomId).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));
    }

    private void isExistedSender(Long senderId) {
        userRepository.findUserById(senderId).orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
    }
}
