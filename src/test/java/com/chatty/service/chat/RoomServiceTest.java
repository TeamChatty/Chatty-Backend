//package com.chatty.service.chat;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//import com.chatty.constants.Code;
//import com.chatty.dto.chat.request.DeleteRoomDto;
//import com.chatty.dto.chat.request.RoomDto;
//import com.chatty.dto.chat.response.RoomResponseDto;
//import com.chatty.entity.chat.ChatRoom;
//import com.chatty.entity.user.User;
//import com.chatty.exception.CustomException;
//import com.chatty.repository.chat.ChatRoomRepository;
//import com.chatty.service.user.UserService;
//import java.util.Optional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class RoomServiceTest {
//
//    @Mock
//    ChatRoomRepository chatRoomRepository;
//
//    @Mock
//    UserService userService;
//
//    @InjectMocks
//    RoomService roomService;
//
//    @Test
//    @DisplayName("채팅방을 만든다.")
//    void createRoom() throws Exception{
//        //given
//        User sender = User.builder().id(1L).build();
//        User receiver = User.builder().id(2L).build();
//        ChatRoom chatRoom = ChatRoom.builder().roomId(1L).sender(sender).receiver(receiver).build();
//        RoomDto roomDto = RoomDto.builder().senderId(1L).receiverId(2L).build();
//
//        when(userService.validateExistUser(anyLong())).thenReturn(User.builder().id(1L).build());
//        when(chatRoomRepository.save(any())).thenReturn(chatRoom);
//
//        //when
//        RoomResponseDto roomResponseDto = roomService.createRoom(roomDto);
//
//        //then
//        assertThat(roomResponseDto.getRoomId()).isEqualTo(chatRoom.getRoomId());
//        assertThat(roomResponseDto.getReceiverId()).isEqualTo(chatRoom.getReceiver().getId());
//        assertThat(roomResponseDto.getSenderId()).isEqualTo(chatRoom.getSender().getId());
//    }
//
//    @Test
//    @DisplayName("채팅방을 만들때. 유저가 존재하지 않으면 예외가 발생한다.")
//    void createRoomNotExistedUser() throws Exception{
//        //given
//        RoomDto roomDto = RoomDto.builder().senderId(1L).receiverId(2L).build();
//
//        when(userService.validateExistUser(anyLong())).thenThrow(new CustomException(Code.NOT_EXIST_USER));
//        //when(chatRoomRepository.save(any())).thenReturn(chatRoom);
//
//        //when,then
//        assertThatThrownBy(() -> roomService.createRoom(roomDto))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("존재 하지 않는 유저 입니다.");
//    }
//
//    @Test
//    @DisplayName("채팅방을 삭제한다.")
//    void deleteRoom() throws Exception{
//        //given
//        User sender = User.builder().id(1L).build();
//        User receiver = User.builder().id(2L).build();
//        ChatRoom chatRoom = ChatRoom.builder().roomId(1L).sender(sender).receiver(receiver).build();
//
//        DeleteRoomDto deleteRoomDto = DeleteRoomDto.builder().roomId(1L).userId(1L).build();
//
//        when(chatRoomRepository.findChatRoomByRoomId(1L)).thenReturn(Optional.of(chatRoom));
//
//        //when
//        RoomResponseDto roomResponseDto = roomService.deleteRoom(deleteRoomDto);
//
//        //then
//        assertThat(roomResponseDto.getRoomId()).isEqualTo(chatRoom.getRoomId());
//        assertThat(roomResponseDto.getSenderId()).isEqualTo(chatRoom.getSender().getId());
//        assertThat(roomResponseDto.getReceiverId()).isEqualTo(chatRoom.getReceiver().getId());
//    }
//
//    @Test
//    @DisplayName("채팅방을 삭제할때, 채팅방에 존재하는 유저가 아니라면 예외가 발생한다.")
//    void deleteRoomNotInChatRoom() throws Exception{
//        //given
//        User sender = User.builder().id(1L).build();
//        User receiver = User.builder().id(2L).build();
//        ChatRoom chatRoom = ChatRoom.builder().roomId(1L).sender(sender).receiver(receiver).build();
//
//        DeleteRoomDto deleteRoomDto = DeleteRoomDto.builder().roomId(1L).userId(3L).build();
//
//        when(chatRoomRepository.findChatRoomByRoomId(1L)).thenReturn(Optional.of(chatRoom));
//
//        //when,then
//        assertThatThrownBy(() ->roomService.deleteRoom(deleteRoomDto))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("유저가 채팅방에 존재하지 않습니다.");
//    }
//
//    @Test
//    @DisplayName("채팅방을 삭제할때, 채팅방이 존재하지 않는다면 예외가 발생한다.")
//    void deleteRoomNotExistedChatRoom() throws Exception{
//        //given
//
//        DeleteRoomDto deleteRoomDto = DeleteRoomDto.builder().roomId(1L).userId(1L).build();
//
//        when(chatRoomRepository.findChatRoomByRoomId(1L)).thenThrow(new CustomException(Code.NOT_FOUND_CHAT_ROOM));
//
//        //when, then
//        assertThatThrownBy(() -> roomService.deleteRoom(deleteRoomDto))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("채팅방이 존재하지 않습니다.");
//    }
//
//    @Test
//    @DisplayName("채팅방을 찾는다.")
//    void findChatRoom() throws Exception{
//        //given
//        User sender = User.builder().id(1L).build();
//        User receiver = User.builder().id(2L).build();
//        ChatRoom chatRoom = ChatRoom.builder().roomId(1L).sender(sender).receiver(receiver).build();
//
//        when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.ofNullable(chatRoom));
//
//        //when
//        RoomResponseDto roomResponseDto = roomService.findChatRoom(anyLong());
//
//        //then
//        assertThat(roomResponseDto.getRoomId()).isEqualTo(chatRoom.getRoomId());
//        assertThat(roomResponseDto.getReceiverId()).isEqualTo(receiver.getId());
//        assertThat(roomResponseDto.getSenderId()).isEqualTo(sender.getId());
//    }
//
//    @Test
//    @DisplayName("채팅방을 찾을때, 채팅방이 존재하지 않는다면 예외가 발생한다.")
//    void findChatRoomNotExistedChatRoom() throws Exception{
//        //given
//        when(chatRoomRepository.findById(anyLong())).thenThrow(new CustomException(Code.NOT_FOUND_CHAT_ROOM));
//
//        //when
//        assertThatThrownBy(() -> roomService.findChatRoom(anyLong()))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("채팅방이 존재하지 않습니다.");
//    }
//
//}
