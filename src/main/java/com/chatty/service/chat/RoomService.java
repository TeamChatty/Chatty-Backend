package com.chatty.service.chat;

import com.chatty.constants.Code;
import com.chatty.dto.chat.request.DeleteRoomDto;
import com.chatty.dto.chat.request.RoomDto;
import com.chatty.dto.chat.response.ChatRoomsResponseDto;
import com.chatty.dto.chat.response.RoomResponseDto;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.user.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public RoomResponseDto createRoom(RoomDto roomDto){
        User receiver = userService.validateExistUser(roomDto.getReceiverId());
        User sender = userService.validateExistUser(roomDto.getSenderId());

        isExistedRoomByUserId(sender,receiver);

        ChatRoom chatRoom = ChatRoom.builder().sender(sender).receiver(receiver).build();
        log.info("채팅방을 생성했습니다.");

        return RoomResponseDto.of(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public RoomResponseDto deleteRoom(DeleteRoomDto deleteRoomDto) {

        ChatRoom chatRoom = isExistedRoomByRoomId(deleteRoomDto.getRoomId());

        isValidUserInRoom(deleteRoomDto.getUserId(), chatRoom);

        chatRoomRepository.delete(chatRoom);
        log.info("채팅방을 삭제했습니다.");

        return RoomResponseDto.of(chatRoom);
    }

    @Transactional
    public RoomResponseDto findChatRoom(long roomId){
        return RoomResponseDto.of(chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM)));
    }

    private void isValidUserInRoom(Long userId, ChatRoom chatRoom){
        if(!(chatRoom.getReceiver().getId() == userId || chatRoom.getSender().getId() == userId)){
            throw new CustomException(Code.NOT_IN_USER_ROOM);
        }
    }

    private void isExistedRoomByUserId(User sender, User receiver){
        Optional<ChatRoom> optionalChatRoom1 = chatRoomRepository.findChatRoomBySenderAndReceiver(sender,receiver);
        Optional<ChatRoom> optionalChatRoom2 = chatRoomRepository.findChatRoomByReceiverAndSender(receiver,sender);

        if(optionalChatRoom1.isPresent() || optionalChatRoom2.isPresent()){
            throw new CustomException(Code.ALREADY_EXIST_CHATROOM);
        }
    }

    private ChatRoom isExistedRoomByRoomId(Long roomId){
        return chatRoomRepository.findChatRoomByRoomId(roomId).orElseThrow(() -> new CustomException(Code.NOT_FOUND_CHAT_ROOM));
    }

    public ChatRoomsResponseDto getRooms(Authentication authentication) {
        User user = userRepository.findUserByMobileNumber(authentication.getName()).orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        List<ChatRoom> rooms = chatRoomRepository.findAllBySender(user);
        List<RoomResponseDto> list = rooms.stream().map(room -> RoomResponseDto.of(room)).collect(Collectors.toList());

        return ChatRoomsResponseDto.of(list);
    }
}
